package com.dentner.admin.api.code;

import com.dentner.core.cmmn.dto.CodeAddDto;
import com.dentner.core.cmmn.dto.CodeDto;
import com.dentner.core.cmmn.mapper.AdminCodeMapper;
import com.dentner.core.cmmn.vo.CodeListVo;
import com.dentner.core.cmmn.vo.CodeVo;
import com.dentner.core.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminCodeService {

	@Autowired
	AdminCodeMapper adminCodeMapper;
    public CodeListVo getCodeList(Integer parentNo, CodeDto codeDto) {
		codeDto.setCodeParentNo(parentNo);
		CodeListVo codeListVo = new CodeListVo();
		codeListVo.setList(adminCodeMapper.selectCodeList(codeDto));
		codeListVo.setCnt(adminCodeMapper.selectCodeListCnt(codeDto));

		return codeListVo;
    }

	public CodeVo getCodeDetail(int codeNo) {
		CodeVo codeVo = new CodeVo();
		codeVo = adminCodeMapper.selectCodeDetail(codeNo);
		codeVo.setDetailList(adminCodeMapper.selectCodeDetailList(codeNo));

		return codeVo;
	}

	public CodeAddDto postCode(CodeAddDto codeAddDto) {
		codeAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		int result = adminCodeMapper.insertCode(codeAddDto);
		return codeAddDto;
	}

	public CodeAddDto putCode(CodeAddDto codeAddDto, int codeNo) {
		codeAddDto.setCodeNo(codeNo);
		int result = adminCodeMapper.updateCode(codeAddDto);
		if(result > 0 ){
			if(codeAddDto.getDetailList() != null && codeAddDto.getDetailList().size() > 0){
				for (int i = 0; i < codeAddDto.getDetailList().size(); i++) {
					CodeAddDto addDto = codeAddDto.getDetailList().get(i);
					if(addDto.getCodeNo() > 0){
						// update
						adminCodeMapper.updateCodeDetail(addDto);
					}else{
						// insert
						addDto.setRegisterNo(SecurityUtil.getMemberNo());
						addDto.setCodeParentNo(codeNo);
						adminCodeMapper.insertCodeDetail(addDto);
					}
				}
			}
		}
		return codeAddDto;
	}

	public int deleteCode(int codeNo) {
		int result = adminCodeMapper.deleteCode(codeNo);
		return codeNo;
	}
}
