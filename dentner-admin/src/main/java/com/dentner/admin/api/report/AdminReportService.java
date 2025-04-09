package com.dentner.admin.api.report;


import com.dentner.core.cmmn.dto.AdminReportDto;
import com.dentner.core.cmmn.mapper.AdminReportMapper;
import com.dentner.core.cmmn.vo.AdminReportListVo;
import com.dentner.core.cmmn.vo.AdminReportVo;
import com.dentner.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReportService {

	@Autowired
	AdminReportMapper adminReportMapper;
	
	public AdminReportListVo getReportList(AdminReportDto adminReportDto) {
		AdminReportListVo adminReportListVo = new AdminReportListVo();
		adminReportListVo.setList(adminReportMapper.selectReportList(adminReportDto));
		adminReportListVo.setCnt(adminReportMapper.selectReportListCnt(adminReportDto));

		return adminReportListVo;
	}

	public AdminReportVo getReportDetail(int reportNo) {
		return adminReportMapper.selectReportDetail(reportNo);
	}

	public int putReportBlock(String memberNoArr, String type) {
		int result = 0;

		if("N".equals(type)){	// 활성화 일때 -> 비활성화 요청 일때 저장
			
			// 2024-09-25 PSH 블랙리스트 중복값 처리
			List<String> inMemberNo = adminReportMapper.selectReportBlockCheck(memberNoArr);
			String[] tempArr = memberNoArr.split(",");
			ArrayList<String> checkMemberNo = new ArrayList<>();
			for (int i=0; i<tempArr.length; i++) {
				if (!inMemberNo.contains(tempArr[i])) {
					checkMemberNo.add(tempArr[i]);
				}
			}
			
			if (checkMemberNo.size() > 0) {
				result = adminReportMapper.insertReportBlock(StringUtils.join(checkMemberNo, ", "), SecurityUtil.getMemberNo());
			} else {
				return 1;
			}
		}else{
			result = adminReportMapper.deleteReportBlock(memberNoArr);
		}

		return result;
	}
}
