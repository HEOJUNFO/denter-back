package com.dentner.admin.api.doc;


import com.dentner.core.cmmn.dto.AdminDocDto;
import com.dentner.core.cmmn.mapper.AdminDocMapper;
import com.dentner.core.cmmn.mapper.AdminTransactionMapper;
import com.dentner.core.cmmn.vo.AdminDocListVo;
import com.dentner.core.cmmn.vo.AdminDocVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminDocService {

	@Autowired
	AdminDocMapper adminDocMapper;

	@Autowired
	AdminTransactionMapper adminTransactionMapper;
	
	public AdminDocListVo getDocList(String requestSe, AdminDocDto adminDocDto) {
		adminDocDto.setRequestSe(requestSe);

		AdminDocListVo adminDocListVo = new AdminDocListVo();
		List<AdminDocVo> docList = new ArrayList<AdminDocVo>();
		docList = adminDocMapper.selectDocList(adminDocDto);

		if(docList != null && docList.size() > 0){
			for (int i = 0; i < docList.size(); i++) {
				docList.get(i).setProstheticsList(adminTransactionMapper.selectTransactionProstheticsList(docList.get(i).getRequestDocGroupNo()));
			}
		}

		adminDocListVo.setList(docList);
		adminDocListVo.setCnt(adminDocMapper.selectDocListCnt(adminDocDto));

		return adminDocListVo;

	}

	public AdminDocVo getDocDetail(int requestDocGroupNo) {
		AdminDocVo adminDocVo = new AdminDocVo();
		adminDocVo = adminDocMapper.selectDocDetail(requestDocGroupNo);

		List<Map<String, Object>> docList = adminTransactionMapper.selectTransactionDoc(requestDocGroupNo);
		if(docList != null && docList.size() > 0){
			for (int i = 0; i < docList.size(); i++) {
				docList.get(i).put("prostheticsList", adminTransactionMapper.selectTransactionProstheticsList(Integer.parseInt(docList.get(i).get("requestDocGroupNo").toString())));
			}
		}
		adminDocVo.setDocList(docList);

		return adminDocVo;
	}
}
