package com.dentner.core.cmmn.mapper;


import com.dentner.core.cmmn.dto.TermsAddDto;
import com.dentner.core.cmmn.vo.TermsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminTermsMapper {
	TermsVo selectTermsInfo(@Param(value = "termsSe") String termsSe);

	int updateTermsInfo(TermsAddDto termsAddDto);
}
