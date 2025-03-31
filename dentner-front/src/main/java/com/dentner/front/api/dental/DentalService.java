package com.dentner.front.api.dental;

import com.dentner.core.cmmn.dto.DentalDto;
import com.dentner.core.cmmn.dto.DesignerDto;
import com.dentner.core.cmmn.dto.ReportDto;
import com.dentner.core.cmmn.mapper.FileMapper;
import com.dentner.core.cmmn.mapper.FrontDentalMapper;
import com.dentner.core.cmmn.mapper.FrontMypageMapper;
import com.dentner.core.cmmn.vo.*;
import com.dentner.core.util.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DentalService {

	@Autowired
	FrontDentalMapper frontDentalMapper;

	@Autowired
	FrontMypageMapper frontMypageMapper;

	@Autowired
	FileMapper fileMapper;

	public DentalListVo getDentalLaboratoryList(DentalDto dentalDto, HttpServletRequest request) {
		String language = request.getHeader("language");
		if(language != null && !"".equals(language)){
			dentalDto.setLanguage(language);
		}

		dentalDto.setRegisterNo(SecurityUtil.getMemberNo());
		dentalDto.setMemberSe(SecurityUtil.getMemberSe());

		DentalListVo dentalListVo = new DentalListVo();
		dentalListVo.setList(frontDentalMapper.selectDentalLaboratoryList(dentalDto));
		dentalListVo.setCnt(frontDentalMapper.selectDentalLaboratoryListCnt(dentalDto));

		return dentalListVo;
	}

	public int postInterestDentalLaboratory(Integer targetNo) {
		return frontDentalMapper.insertInterestDental(SecurityUtil.getMemberNo(), targetNo, "A");
	}

	public int deleteInterestDentalLaboratory(Integer targetNo) {
		return frontDentalMapper.deleteInterestDental(SecurityUtil.getMemberNo(), targetNo, "A");
	}

	public int postBlockDentalLaboratory(Integer targetNo) {
		// 차단 디자이너 등록시 관심 디자이너로 등록되어있던 데이터 삭제
		frontDentalMapper.deleteInterestDental(SecurityUtil.getMemberNo(), targetNo, "A");

		int result = 0;
		result = frontDentalMapper.selectBlockDental(SecurityUtil.getMemberNo(), targetNo, "A");

		if(result == 0){
			frontDentalMapper.insertBlockDental(SecurityUtil.getMemberNo(), targetNo, "A");
		}
		return 1;
	}

	public int postReportDental(Integer targetNo, ReportDto reportDto) {
		reportDto.setMemberNo(SecurityUtil.getMemberNo());
		reportDto.setTargetNo(targetNo);
		//reportDto.setReportTp("A");

		return frontDentalMapper.insertReportDental(reportDto);
	}

	public DentalVo getDentalLaboratoryDetail(Integer memberNo) {
		DentalVo dentalVo = new DentalVo();
		dentalVo = frontDentalMapper.selectDentalLaboratoryDetail(memberNo, SecurityUtil.getMemberNo());
		if(dentalVo != null){
			String memberSe = dentalVo.getMemberSe();
			if(!"B".equals(memberSe)){
				dentalVo.setImageList(fileMapper.selectFileList(memberNo, "F"));
			}else{
				dentalVo.setImageList(fileMapper.selectFileList(memberNo, "M"));
			}
		}
		return dentalVo;
	}

	public DesignerListVo getDentalDesignerList(DesignerDto designerDto) {
		designerDto.setRegisterNo(SecurityUtil.getMemberNo());
		designerDto.setMemberSe(SecurityUtil.getMemberSe());

		if (designerDto.getProstheticsFilter() != null && !designerDto.getProstheticsFilter().isEmpty()) {
			designerDto.setProstheticsFilterList(Arrays.asList(designerDto.getProstheticsFilter().split(",")));
		}

		DesignerListVo designerListVo = new DesignerListVo();

		designerListVo.setList(frontDentalMapper.selectDentalDesignerList(designerDto));
		designerListVo.setCnt(frontDentalMapper.selectDentalDesignerListCnt(designerDto));

		return designerListVo;
	}

	public int postInterestDentalDesigner(Integer targetNo) {
		return frontDentalMapper.insertInterestDental(SecurityUtil.getMemberNo(), targetNo, "B");
	}

	public int deleteInterestDentalDesigner(Integer targetNo) {
		return frontDentalMapper.deleteInterestDental(SecurityUtil.getMemberNo(), targetNo, "B");
	}

	public int postBlockDentalDesigner(Integer targetNo) {
		// 차단 디자이너 등록시 관심 디자이너로 등록되어있던 데이터 삭제
		frontDentalMapper.deleteInterestDental(SecurityUtil.getMemberNo(), targetNo, "B");

		int result = 0;
		result = frontDentalMapper.selectBlockDental(SecurityUtil.getMemberNo(), targetNo, "B");

		if(result == 0){
			frontDentalMapper.insertBlockDental(SecurityUtil.getMemberNo(), targetNo, "B");
		}
		return 1;
	}

	public DesignerVo getDentalDesignerDetail(Integer memberNo) {
		DesignerVo designerVo = new DesignerVo();
		designerVo = frontDentalMapper.selectDentalDesignerDetail(memberNo, SecurityUtil.getMemberNo());

		if(designerVo != null){
			String memberSe = designerVo.getMemberSe();
			if(!"B".equals(memberSe)){
				designerVo.setImageList(fileMapper.selectFileList(memberNo, "F"));
				List<S3FileListVO> fileListVO = fileMapper.selectFileLists(memberNo, "O");
				if(fileListVO.size() > 0){
					for (int i = 0; i < fileListVO.size(); i++) {
						fileListVO.get(i).setCadList(fileMapper.selectFileCadList(memberNo, "P", fileListVO.get(i).getFileOrdr()));
					}
					designerVo.setFileList(fileListVO);
				}
			}else{
				designerVo.setImageList(fileMapper.selectFileList(memberNo, "M"));
			}
			designerVo.setTypeList(frontMypageMapper.selectMemberTypeList(memberNo));
		}
		return designerVo;
	}

	public int deleteBlockDentalDesigner(Integer targetNo) {
		return frontDentalMapper.deleteBlockDental(SecurityUtil.getMemberNo(), targetNo, "B");
	}

	public int deleteBlockDentalLaboratory(Integer targetNo) {
		return frontDentalMapper.deleteBlockDental(SecurityUtil.getMemberNo(), targetNo, "A");
	}

	public int postBlockDentalRequest(Integer targetNo) {
		int result = 0;
		result = frontDentalMapper.selectBlockDental(SecurityUtil.getMemberNo(), targetNo, "C");

		if(result == 0){
			frontDentalMapper.insertBlockDental(SecurityUtil.getMemberNo(), targetNo, "C");
		}
		return 1;
	}

	public int deleteBlockDentalRequest(Integer targetNo) {
		return frontDentalMapper.deleteBlockDental(SecurityUtil.getMemberNo(), targetNo, "C");
	}
}
