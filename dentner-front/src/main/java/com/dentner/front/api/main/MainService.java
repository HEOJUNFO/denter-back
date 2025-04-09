package com.dentner.front.api.main;

import com.dentner.core.cmmn.mapper.FileMapper;
import com.dentner.core.cmmn.mapper.FrontMainMapper;
import com.dentner.core.cmmn.vo.BannerVo;
import com.dentner.core.cmmn.vo.BbsVo;
import com.dentner.core.cmmn.vo.StatVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MainService {

	@Autowired
	FrontMainMapper frontMainMapper;

	@Autowired
	FileMapper fileMapper;

    public List<BannerVo> getBanner() {
        return frontMainMapper.selectBanner();
    }

	public StatVo getStat() {
		return frontMainMapper.selectStat();
	}

	public List<BbsVo> getBbs(String bbsTp) {
		return frontMainMapper.selectBbs(bbsTp);
	}
}
