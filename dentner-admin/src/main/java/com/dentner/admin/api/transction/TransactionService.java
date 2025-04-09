package com.dentner.admin.api.transction;

import com.dentner.core.cmmn.dto.AdminRequestFormDto;
import com.dentner.core.cmmn.mapper.AdminTransactionMapper;
import com.dentner.core.cmmn.mapper.FileMapper;
import com.dentner.core.cmmn.service.S3Upload;
import com.dentner.core.cmmn.vo.AdminRequestFormListVo;
import com.dentner.core.cmmn.vo.AdminRequestFormVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionService {

	@Autowired
	AdminTransactionMapper adminTransactionMapper;

	@Autowired
	FileMapper fileMapper;

	private final S3Upload s3Upload;


	public AdminRequestFormListVo getTransactionFormList(String requestFormSe, AdminRequestFormDto adminRequestFormDto) {
		adminRequestFormDto.setRequestFormSe(requestFormSe);

		AdminRequestFormListVo requestFormListVo = new AdminRequestFormListVo();
		requestFormListVo.setList(adminTransactionMapper.selectTransactionList(adminRequestFormDto));
		requestFormListVo.setCnt(adminTransactionMapper.selectTransactionListCnt(adminRequestFormDto));

		return requestFormListVo;
	}

	public AdminRequestFormVo getTransactionDetail(String type, int requestFormNo) {
		AdminRequestFormVo adminRequestFormVo = new AdminRequestFormVo();
		adminRequestFormVo = adminTransactionMapper.selectTransactionDetail(type, requestFormNo);
		if("A".equals(type)){
			requestFormNo = adminRequestFormVo.getRequestFormNo();
		}
		List<Map<String, Object>> docList = adminTransactionMapper.selectTransactionDocGroup(requestFormNo);
		if(docList != null && docList.size() > 0){
			for (int i = 0; i < docList.size(); i++) {
				docList.get(i).put("prostheticsList", adminTransactionMapper.selectTransactionProstheticsList(Integer.parseInt(docList.get(i).get("requestDocGroupNo").toString())));
			}
		}
		adminRequestFormVo.setDocList(docList);
		adminRequestFormVo.setReplyList(adminTransactionMapper.selectTransactionReplyList(requestFormNo));

		return adminRequestFormVo;
	}

	public List<AdminRequestFormVo>  getTransactionExcelList(String requestFormSe, AdminRequestFormDto adminRequestFormDto) {
		adminRequestFormDto.setRequestFormSe(requestFormSe);
		return adminTransactionMapper.selectTransctionExcelList(adminRequestFormDto);
	}

}
