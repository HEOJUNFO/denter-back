package com.dentner.front.api.join;

import com.dentner.core.cmmn.dto.MemberAddDto;
import com.dentner.core.cmmn.dto.PasswordDto;
import com.dentner.core.cmmn.mapper.FileMapper;
import com.dentner.core.cmmn.mapper.JoinMapper;
import com.dentner.core.cmmn.service.S3Upload;
import com.dentner.core.cmmn.vo.FrontTermsVo;
import com.dentner.core.cmmn.vo.S3FileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class JoinService {

	@Autowired
	JoinMapper joinMapper;

	@Autowired
	FileMapper fileMapper;

	private final S3Upload s3Upload;

	@Autowired
	private PasswordEncoder passwordEncoder;
	public MemberAddDto postMember(MultipartFile licenseFile,
								   MultipartFile businessFile, MemberAddDto memberAddDto) throws Exception{
		// 1. 중복확인 추가
		int cnt = joinMapper.selectMember(memberAddDto);
		if(cnt > 0){
			throw new Exception("등록된 메일이 있습니다.");
		}

		int cntPhone = joinMapper.selectMemberPhone(memberAddDto);
		if(cntPhone > 0){
			throw new Exception("등록된 전화번호가 있습니다.");
		}
		memberAddDto.setMemberTp("A");

		if(memberAddDto.getMemberPassword() != null){
			memberAddDto.setMemberPassword(passwordEncoder.encode(memberAddDto.getMemberPassword()));
		}

		// 치기공소일때 이름을 대표자 이름으로 저장
		if("B".equals(memberAddDto.getMemberSe())){
			memberAddDto.setMemberName(memberAddDto.getMemberRepresentativeName());
		}
		int result = joinMapper.insertMember(memberAddDto);

		if(result == 0){
			throw new Exception("회원가입에 실패하였습니다. 관리자에게 문의하십시오.");
		}

		if(licenseFile != null){
			S3FileVO fileVO = s3Upload.upload(licenseFile);
			fileVO.setFileFromNo(memberAddDto.getMemberNo());
			fileVO.setFileSe("A");
			fileVO.setRegisterNo(memberAddDto.getMemberNo());
			fileMapper.insertFile(fileVO);
		}
		if(businessFile != null){
			S3FileVO fileVO = s3Upload.upload(businessFile);
			fileVO.setFileFromNo(memberAddDto.getMemberNo());
			fileVO.setFileSe("B");
			fileVO.setRegisterNo(memberAddDto.getMemberNo());
			fileMapper.insertFile(fileVO);
		}

		if(memberAddDto.getSocialSe() != null &&
				!memberAddDto.getSocialSe().equals("")){
			joinMapper.insertSocialMember(memberAddDto);
		}

		// 치과기공소, 치자이너 가입시 프로필 정보도 등록
		// 2024-09-11 의뢰인도 포함되도록 수정
		//if("B".equals(memberAddDto.getMemberSe()) || "C".equals(memberAddDto.getMemberSe())){
		joinMapper.insertProfileMember(memberAddDto);
		//}

		// 의뢰인, 치자이너 가입시 sw 정보도 등록
		if("A".equals(memberAddDto.getMemberSe()) || "C".equals(memberAddDto.getMemberSe())){
			joinMapper.insertSwMember(memberAddDto);
			//joinMapper.insertAlarmSetting(memberAddDto);
			joinMapper.insertAlarmSettingNew(memberAddDto);
		}

		memberAddDto.setMemberPassword("");

		return memberAddDto;
	}

    public FrontTermsVo getTerms() {
		FrontTermsVo frontTermsVo = new FrontTermsVo();
		frontTermsVo.setUseTerms(joinMapper.selectTerms("A"));
		frontTermsVo.setPrivateTerms(joinMapper.selectTerms("B"));
		frontTermsVo.setMarketingTerms(joinMapper.selectTerms("C"));
		frontTermsVo.setPrivateConsignmentTerms(joinMapper.selectTerms("D"));

		return frontTermsVo;
    }

	public int getDupNickName(MemberAddDto memberAddDto) {
		return joinMapper.selectDupNickName(memberAddDto.getMemberNickName());
	}

	public int getDupEmail(MemberAddDto memberAddDto) {
		return joinMapper.selectDupEmail(memberAddDto.getMemberEmail());
	}

	public MemberAddDto postCountryMember(MultipartFile businessFile, MemberAddDto memberAddDto) throws Exception{
		// 1. 중복확인 추가
		int cnt = joinMapper.selectMember(memberAddDto);
		if(cnt > 0){
			throw new Exception("등록된 메일이 있습니다.");
		}

		int cntPhone = joinMapper.selectMemberPhone(memberAddDto);
		if(cntPhone > 0){
			throw new Exception("등록된 전화번호가 있습니다.");
		}
		memberAddDto.setMemberSe("A");
		memberAddDto.setMemberTp("B");
		if(memberAddDto.getMemberPassword() != null){
			memberAddDto.setMemberPassword(passwordEncoder.encode(memberAddDto.getMemberPassword()));
		}
		int result = joinMapper.insertMember(memberAddDto);

		if(result == 0){
			throw new Exception("회원가입에 실패하였습니다. 관리자에게 문의하십시오.");
		}
		
		if(businessFile != null){
			S3FileVO fileVO = s3Upload.upload(businessFile);
			fileVO.setFileFromNo(memberAddDto.getMemberNo());
			fileVO.setFileSe("B");
			fileVO.setRegisterNo(memberAddDto.getMemberNo());
			fileMapper.insertFile(fileVO);
		}
		
		if(memberAddDto.getSocialSe() != null &&
				!memberAddDto.getSocialSe().equals("")){
			joinMapper.insertSocialMember(memberAddDto);
		}

		joinMapper.insertProfileMember(memberAddDto);

		// 의뢰인, 치자이너 가입시 sw 정보도 등록
		joinMapper.insertSwMember(memberAddDto);
		//joinMapper.insertAlarmSetting(memberAddDto);
		joinMapper.insertAlarmSettingNew(memberAddDto);

		return memberAddDto;
	}

    public String getId(String token) {
		return joinMapper.selectEmail(token);
    }

	public int putPassword(PasswordDto passwordDto) {
		passwordDto.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
		return joinMapper.updatePassword(passwordDto);
	}
}
