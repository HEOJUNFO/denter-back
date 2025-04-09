package com.dentner.admin.api.terms;


import com.dentner.core.cmmn.dto.TermsAddDto;
import com.dentner.core.cmmn.mapper.AdminTermsMapper;
import com.dentner.core.cmmn.vo.TermsVo;
import com.dentner.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminTermsService {

	@Autowired
	AdminTermsMapper adminTermsMapper;

	public TermsVo getTermsInfo(String termsSe) {
		return adminTermsMapper.selectTermsInfo(termsSe);
	}

	public int putTermsInfo(String termsSe, TermsAddDto termsAddDto) {
		termsAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		termsAddDto.setTermsSe(termsSe);
		return adminTermsMapper.updateTermsInfo(termsAddDto);
	}
}
