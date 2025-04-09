package com.dentner.core.cmmn.mapper;

import com.dentner.core.cmmn.dto.CodeAddDto;
import com.dentner.core.cmmn.dto.CodeDto;
import com.dentner.core.cmmn.vo.CodeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminCodeMapper {
    List<CodeVo> selectCodeList(CodeDto codeDto);

    int selectCodeListCnt(CodeDto codeDto);

    CodeVo selectCodeDetail(@Param("codeNo") int codeNo);

    List<CodeVo> selectCodeDetailList(@Param("codeNo") int codeNo);

    int insertCode(CodeAddDto codeAddDto);

    int updateCode(CodeAddDto codeAddDto);

    int updateCodeDetail(CodeAddDto addDto);

    int insertCodeDetail(CodeAddDto addDto);

    int deleteCode(@Param("codeNo") int codeNo);
}
