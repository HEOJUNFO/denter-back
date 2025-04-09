package com.dentner.front.api.mypage;

import com.dentner.core.cmmn.dto.*;
import com.dentner.core.cmmn.mapper.FileMapper;
import com.dentner.core.cmmn.mapper.FrontMypageMapper;
import com.dentner.core.cmmn.service.MemberService;
import com.dentner.core.cmmn.service.S3Upload;
import com.dentner.core.cmmn.vo.*;
import com.dentner.core.config.JwtTokenProvider;
import com.dentner.core.util.SecurityUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {

	@Autowired
	FrontMypageMapper frontMypageMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	FileMapper fileMapper;

	private final S3Upload s3Upload;

	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenProvider jwtTokenProvider;
	private final MemberService memberService;


	public MemberVo getMypageInfo() {
		return frontMypageMapper.selectMypage(SecurityUtil.getMemberNo(), SecurityUtil.getMemberSe());
	}

	public MemberAddDto putMypageInfo(MultipartFile businessFile, MemberAddDto memberAddDto) throws Exception{
		memberAddDto.setMemberNo(SecurityUtil.getMemberNo());

		// 1. 삭제된 파일이 있는지 확인.
		if(memberAddDto.getFileDel() != null && !"".equals(memberAddDto.getFileDel())){
			fileMapper.deleteFileArr(memberAddDto.getFileDel());
		}

		int result = 0;
		result = frontMypageMapper.updateMypageMemberInfo(memberAddDto);

		if(result > 0){
			if(businessFile != null){
				S3FileVO fileVO = s3Upload.upload(businessFile);
				fileVO.setFileFromNo(memberAddDto.getMemberNo());
				fileVO.setFileSe("B");
				fileVO.setRegisterNo(memberAddDto.getMemberNo());
				fileMapper.insertFile(fileVO);
			}
		}else{
			memberAddDto = null;
		}
		return memberAddDto;
	}

	public int putMypageOut() {
		int result = 0;
		boolean bool = false;
		// 1. 거래완료 내역이 있는지 확인.
		result = frontMypageMapper.selectMypageOutEnd(SecurityUtil.getMemberNo());
		if(result == 0){
			// 거래중 또는 거래완료가 없을 때
			// 2. 의뢰인의 경우 충전한 마일리지가 있는지 확인.
			int cnt = frontMypageMapper.selectMypageOutMileage(SecurityUtil.getMemberNo());
			if(cnt > 0){

			}
			result = frontMypageMapper.updateMypageOut(SecurityUtil.getMemberNo());
		}else{
			// 거래완료 건수가 있는지
			bool = true;
			result = 0;
		}
		if(bool){
			frontMypageMapper.insertMemberOut(SecurityUtil.getMemberNo());
		}
		return result;
	}

	public MemberProfileVo getMypageProfile() {
		MemberProfileVo memberProfileVo = new MemberProfileVo();
		memberProfileVo = frontMypageMapper.selectMypageProfile(SecurityUtil.getMemberNo(), SecurityUtil.getMemberSe());
		if("B".equals(memberProfileVo.getMemberSe())){
			memberProfileVo.setImageList(fileMapper.selectFileList(SecurityUtil.getMemberNo(), "L"));
		}else if("C".equals(memberProfileVo.getMemberSe())){
			memberProfileVo.setImageList(fileMapper.selectFileList(SecurityUtil.getMemberNo(), "F"));
			List<S3FileListVO> fileListVO = fileMapper.selectFileLists(SecurityUtil.getMemberNo(), "O");
			if(fileListVO.size() > 0){
				for (int i = 0; i < fileListVO.size(); i++) {
					fileListVO.get(i).setCadList(fileMapper.selectFileCadList(SecurityUtil.getMemberNo(), "P", fileListVO.get(i).getFileOrdr()));
				}
				memberProfileVo.setFileList(fileListVO);
			}
		}

		memberProfileVo.setTypeList(frontMypageMapper.selectMemberTypeList(SecurityUtil.getMemberNo()));
		return memberProfileVo;
	}

	public MemberProfileAddDto putMypageProfile(MultipartFile photo,
												List<MultipartFile> images,
												List<MultipartFile> files,
												MemberProfileAddDto memberProfileAddDto) {
		memberProfileAddDto.setMemberNo(SecurityUtil.getMemberNo());
		String memberSe = memberProfileAddDto.getMemberSe();
		System.out.println("memberSe = " + memberSe);
		int result = 0;
		// 닉네임 수정 2024-09-11 프로필쪽에서 닉네임 수정
		//result = frontMypageMapper.updateMypageMember(memberProfileAddDto);
		frontMypageMapper.updateMypageProfile(memberProfileAddDto);
		if("B".equals(memberSe)){	// 치과 기공소

		}else if("A".equals(memberSe) || "C".equals(memberSe)){
			// 선호 CAD/SW 정보
			frontMypageMapper.updateMypageSw(memberProfileAddDto);

			if("C".equals(memberSe)){
				//frontMypageMapper.updateMypageProfile(memberProfileAddDto);
			}
		}

		if(photo != null){
			// 단건일 경우 이미지가 있을 경우 기존 파일을 삭제한다.
			S3FileVO delFileVO = new S3FileVO();
			if("B".equals(memberSe)){
				delFileVO.setFileSe("L");
			}else{
				delFileVO.setFileSe("D");
			}
			delFileVO.setFileFromNo(SecurityUtil.getMemberNo());
			fileMapper.deleteFile(delFileVO);

			S3FileVO fileVO = s3Upload.upload(photo);
			fileVO.setFileFromNo(memberProfileAddDto.getMemberNo());
			if("B".equals(memberSe)){
				fileVO.setFileSe("L");
			}else {
				fileVO.setFileSe("D");
			}
			fileVO.setRegisterNo(memberProfileAddDto.getMemberNo());
			fileMapper.insertFile(fileVO);

			// jjchoi return 데이타에 이미지 추가
			memberProfileAddDto.setMemberProfileImage(fileVO.getFileUrl());
		}

		// 치과 기공소나 치자이너 일때 회사사진 or 포토폴리오 등록
		if("B".equals(memberSe) || "C".equals(memberSe)){
			// 1. 삭제된 파일이 있는지 확인.
			if(memberProfileAddDto.getFileDel() != null && !"".equals(memberProfileAddDto.getFileDel())){
				fileMapper.deleteFileArr(memberProfileAddDto.getFileDel());
			}

			if(images != null){
				for (int i = 0; i < images.size(); i++) {
					S3FileVO fileVO = s3Upload.upload(images.get(i));
					fileVO.setFileFromNo(memberProfileAddDto.getMemberNo());
					if("B".equals(memberSe)){
						fileVO.setFileSe("M");
					}else if("C".equals(memberSe)){
						fileVO.setFileSe("F");
					}

					fileVO.setRegisterNo(memberProfileAddDto.getMemberNo());
					fileMapper.insertFile(fileVO);
				}
			}
			// 3d 포트폴리오 파일이 있는지 확인.
			// cad 파일 업로드
			if(memberProfileAddDto.getFileList() != null &&
					!"".equals(memberProfileAddDto.getFileList())){
				ObjectMapper objectMapper = new ObjectMapper();
				List<ThreeFileDto> fileList;

				try {
					String jsonString = memberProfileAddDto.getFileList();
					fileList = objectMapper.readValue(jsonString, new TypeReference<List<ThreeFileDto>>(){});

					// fileOrdr 값 기준으로 오름차순 정렬
					Collections.sort(fileList, (f1, f2) -> Integer.compare(f1.getFileOrdr(), f2.getFileOrdr()));
					int fileOrdr = fileMapper.selectFileOrdr(memberProfileAddDto.getMemberNo(),"O");
					for (ThreeFileDto threeFile : fileList) {
						System.out.println("getFileOrdr = " + threeFile.getFileOrdr());
						String fileName = threeFile.getFileName();
						MultipartFile file = findFileByName(files, fileName);
						if (file != null) {
							S3FileVO fileVO = s3Upload.uploadToOrgName(file, threeFile.getFileOrgName());
							fileVO.setFileFromNo(memberProfileAddDto.getMemberNo());
							if("image".equals(threeFile.getFileTp())){
								fileVO.setFileSe("O");
								if(fileOrdr != threeFile.getFileOrdr()){
									fileOrdr = fileMapper.selectFileOrdr(memberProfileAddDto.getMemberNo(),"O");
								}
							}else{
								fileVO.setFileSe("P");
							}

							fileVO.setRegisterNo(SecurityUtil.getMemberNo());
							fileVO.setFileOrdr(fileOrdr);
							result =+ fileMapper.insertFileOrdr(fileVO);
						}
					}
				} catch (JsonProcessingException e) {
					e.printStackTrace();
				}
			}
//			if(files != null){
//				for (int i = 0; i < files.size(); i++) {
//					S3FileVO fileVO = s3Upload.upload(files.get(i));
//					fileVO.setFileFromNo(memberProfileAddDto.getMemberNo());
//					if("B".equals(memberSe)){
//						fileVO.setFileSe("M");
//					}else if("C".equals(memberSe)){
//						fileVO.setFileSe("F");
//					}
//
//					fileVO.setRegisterNo(memberProfileAddDto.getMemberNo());
//					fileMapper.insertFile(fileVO);
//				}
//			}
		}

		MemberProfileVo memberProfileVo = new MemberProfileVo();
		memberProfileVo = frontMypageMapper.selectMypageProfile(SecurityUtil.getMemberNo(), SecurityUtil.getMemberSe());

		memberProfileAddDto.setSwNoName(memberProfileVo.getSwName());
		return memberProfileAddDto;
	}

	public InterestListVo getInterestList(String interestSe, InterestDto interestDto) {
		interestDto.setMemberNo(SecurityUtil.getMemberNo());
		if("A".equals(interestSe)){	// 치과 기공소 일때
			interestDto.setMemberSe("B");
		}else{
			interestDto.setMemberSe("C");
		}

		interestDto.setInterestSe(interestSe);

		InterestListVo interestListVo = new InterestListVo();
		interestListVo.setList(frontMypageMapper.selectInterestList(interestDto));
		interestListVo.setCnt(frontMypageMapper.selectInterestListCnt(interestDto));

		return interestListVo;
	}

	public BlockListVo getBlockList(String blockSe, BlockDto blockDto) {
		blockDto.setMemberNo(SecurityUtil.getMemberNo());
		if("A".equals(blockSe)){	// 치과 기공소 일때
			blockDto.setMemberSe("B");
		}else if("B".equals(blockSe)){	// 치자이너
			blockDto.setMemberSe("C");
		}else{
			blockDto.setMemberSe("A");
		}
		blockDto.setBlockSe(blockSe);

		BlockListVo blockListVo = new BlockListVo();
		blockListVo.setList(frontMypageMapper.selectBlockList(blockDto));
		blockListVo.setCnt(frontMypageMapper.selectBlockListCnt(blockDto));

		return blockListVo;
	}

	public int putMypageType(MemberTypeDto memberTypeDto) {
		memberTypeDto.setMemberNo(SecurityUtil.getMemberNo());
		return frontMypageMapper.updateMypageType(memberTypeDto);
	}

	public List<MemberTypeVo> getMypageTypeList() {
		return frontMypageMapper.selectMemberTypeList(SecurityUtil.getMemberNo());
	}

	public ReviewListVo getReviewList(ReviewDto reviewDto) {
		reviewDto.setMemberNo(SecurityUtil.getMemberNo());
		reviewDto.setMemberSe(SecurityUtil.getMemberSe());

		ReviewListVo reviewListVo = new ReviewListVo();
		List<ReviewVo> reviewList = new ArrayList<ReviewVo>();

		reviewList = frontMypageMapper.selectReviewList(reviewDto);
		if(reviewList.size() > 0){
			for (int i = 0; i < reviewList.size(); i++) {
				reviewList.get(i).setFileList(fileMapper.selectFileList(reviewList.get(i).getReviewNo(), "C"));
			}

		}
		reviewListVo.setList(reviewList);
		reviewListVo.setCnt(frontMypageMapper.selectReviewListCnt(reviewDto));

		return reviewListVo;
	}

	public int deleteMypageReview(Integer reviewNo) {
		return frontMypageMapper.deleteMypageReview(reviewNo, SecurityUtil.getMemberNo());
	}

	public int putReview(Integer reviewNo, List<MultipartFile> images, ReviewDto reviewDto) {
		reviewDto.setReviewNo(reviewNo);
		reviewDto.setMemberNo(SecurityUtil.getMemberNo());

		if(reviewDto.getFileDel() != null && !"".equals(reviewDto.getFileDel())){
			fileMapper.deleteFileArr(reviewDto.getFileDel());
		}

		int result = 0;
		result = frontMypageMapper.updateReview(reviewDto);

		if(images != null){
			for (int i = 0; i < images.size(); i++) {
				S3FileVO fileVO = s3Upload.upload(images.get(i));
				fileVO.setFileFromNo(reviewDto.getReviewNo());
				fileVO.setFileSe("C");
				fileVO.setRegisterNo(SecurityUtil.getMemberNo());
				fileMapper.insertFile(fileVO);
			}
		}

		return result;
	}

	public ReviewVo getReviewDetail(Integer reviewNo) {
		ReviewVo reviewVo = new ReviewVo();
		reviewVo = frontMypageMapper.selectReviewDetail(reviewNo, SecurityUtil.getMemberNo(), SecurityUtil.getMemberSe());
		reviewVo.setFileList(fileMapper.selectFileList(reviewNo, "C"));
		return reviewVo;
	}

    public int putMypagePhone(PhoneDto phoneDto) throws Exception {
		phoneDto.setMemberNo(SecurityUtil.getMemberNo());
		
		int i = frontMypageMapper.selectPhoneCheck(phoneDto);
		if (i > 0) {
			throw new Exception("이미 등록된 전화번호입니다.");
		} else {
			return frontMypageMapper.updateMypagePhone(phoneDto);
		}

    }

	public int putMypagePassword(PasswordDto passwordDto) {
		int result = 0;
		passwordDto.setMemberNo(SecurityUtil.getMemberNo());
		String password = frontMypageMapper.selectMypagePassword(passwordDto);
		if (!passwordEncoder.matches(passwordDto.getOldPassword(), password)) {
			throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
		}

		passwordDto.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
		result = frontMypageMapper.updateMypagePassword(passwordDto);
		return result;
	}


	public int postProfileDesigner() throws Exception{
		int result = 0;
		// 1. 치자이너 프로필이 등록되어있는지 확인
		int cnt = frontMypageMapper.selectProfileDesigner(SecurityUtil.getMemberNo());
		if(cnt > 1){
			throw new Exception("치자이너 프로필이 이미 등록되어 있습니다.");
		}

		result = frontMypageMapper.insertProfileDesigner(SecurityUtil.getMemberNo(), "C");
		result = frontMypageMapper.insertProfileDesignerSw(SecurityUtil.getMemberNo());
		// 치기공쪽에 이름 추가해주기.
		frontMypageMapper.updateProfileDesignerName(SecurityUtil.getMemberNo());

		return result;
	}

	public TokenDto putProfileChange() {
		TokenDto tokenDto = null;
		String memberSe = SecurityUtil.getMemberSe();
		String targetSe = "";

		if("B".equals(memberSe)){	// 치기공소 일때
			targetSe = "C";
		}else{ // 치자이너 일때
			targetSe = "B";
		}
		MemberVo memberVo = memberService.getUserInfoNo(SecurityUtil.getMemberNo(), targetSe);
		tokenDto = jwtTokenProvider.generateChangeToken(memberVo);
		tokenDto.setUserInfo(memberVo);
		return tokenDto;
	}

    public List<AlarmCodeVo> getAlarmList(String type) {
		String memberSe = SecurityUtil.getMemberSe();
		if("C".equals(memberSe)){
			memberSe = "B";
		}

		//return frontMypageMapper.selectAlarmList(SecurityUtil.getMemberNo(), memberSe, type);
		return frontMypageMapper.selectAlarmListNew(SecurityUtil.getMemberNo(), memberSe, type);
    }

	public int putAlarm(String code, String type) {
		int result = 0;
		if("Y".equals(type)){
			result = frontMypageMapper.insertAlarmSetting(code, SecurityUtil.getMemberNo());
		}else{
			result = frontMypageMapper.deleteAlarmSetting(code, SecurityUtil.getMemberNo());
		}

		return result;
	}

    public int putShow(String type, String memberSe) {
		return frontMypageMapper.updateProfileShow(type, memberSe, SecurityUtil.getMemberNo());
    }

	private MultipartFile findFileByName(List<MultipartFile> files, String fileName) {
		return files.stream()
				.filter(file -> file.getOriginalFilename().equals(fileName))
				.findFirst()
				.orElse(null);
	}

	public ReviewListVo getReviewMemberList(ReviewDto reviewDto, Integer memberNo) {
		reviewDto.setMemberNo(memberNo);
		ReviewListVo reviewListVo = new ReviewListVo();
		List<ReviewVo> reviewList = new ArrayList<ReviewVo>();

		reviewList = frontMypageMapper.selectReviewMemberList(reviewDto);
		if(reviewList.size() > 0){
			for (int i = 0; i < reviewList.size(); i++) {
				reviewList.get(i).setFileList(fileMapper.selectFileList(reviewList.get(i).getReviewNo(), "C"));
			}

		}
		reviewListVo.setList(reviewList);
		reviewListVo.setCnt(frontMypageMapper.selectReviewMemberListCnt(reviewDto));

		return reviewListVo;
	}

	public int putAllAlarm(String code, String type) {
		int result = 0;
		if("Y".equals(type)){
			result = frontMypageMapper.insertAlarmAllSetting(code, SecurityUtil.getMemberNo(), SecurityUtil.getMemberSe());
		}else{
			result = frontMypageMapper.deleteAlarmAllSetting(code, SecurityUtil.getMemberNo(), SecurityUtil.getMemberSe());
		}

		return result;
	}
}
