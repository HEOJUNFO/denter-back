package com.dentner.front.api.bbs;

import com.dentner.core.cmmn.dto.BbsDto;
import com.dentner.core.cmmn.mapper.FrontBbsMapper;
import com.dentner.core.cmmn.vo.BbsListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BbsService {

	@Autowired
	FrontBbsMapper frontBbsMapper;

	public BbsListVo getBbs(String bbsSe, String bbsTp, BbsDto bbsDto) {
		bbsDto.setBbsSe(bbsSe);
		bbsDto.setBbsTp(bbsTp);

		BbsListVo bbsListVo = new BbsListVo();
		bbsListVo.setList(frontBbsMapper.selectBbs(bbsDto));
		bbsListVo.setCnt(frontBbsMapper.selectBbsCnt(bbsDto));

		return bbsListVo;
	}
}
