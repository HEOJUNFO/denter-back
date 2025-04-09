package com.dentner.admin.api.bbs;


import com.dentner.admin.api.common.CommonService;
import com.dentner.core.cmmn.dto.AlarmAddDto;
import com.dentner.core.cmmn.dto.BbsAddDto;
import com.dentner.core.cmmn.dto.BbsDto;
import com.dentner.core.cmmn.dto.PushDto;
import com.dentner.core.cmmn.mapper.AdminBbsMapper;
import com.dentner.core.cmmn.vo.BbsListVo;
import com.dentner.core.cmmn.vo.BbsVo;
import com.dentner.core.util.SecurityUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminBbsService {

	@Autowired
	AdminBbsMapper adminBbsMapper;

	@Resource(name= "commonService")
	CommonService commonService;

	public BbsListVo getBbsList(BbsDto bbsDto) {
		BbsListVo bbsListVo = new BbsListVo();
		bbsListVo.setList(adminBbsMapper.selectBbsList(bbsDto));
		bbsListVo.setCnt(adminBbsMapper.selectBbsListCnt(bbsDto));

		return bbsListVo;
	}

	public BbsVo getBbsDetail(Integer bbsNo) {
		return adminBbsMapper.selectBbsDetail(bbsNo);
	}

	public int postBbs(String bbsSe, BbsAddDto bbsAddDto) {
		bbsAddDto.setBbsSe(bbsSe);
		bbsAddDto.setRegisterNo(SecurityUtil.getMemberNo());

		int result = adminBbsMapper.insertBbs(bbsAddDto);
		if(result > 0 ){
			// 2025-03-05 cjj 로그인한 사람이 국내/해외 의뢰인인지 확인.
			// 회원유형 (A:한국인, B:외국인)
			String memberTp = commonService.selectMemberTp(SecurityUtil.getMemberNo());
			String msg = "";
			if("A".equals(memberTp)){
				msg = "덴트너소식";
			}else{
				msg = "Dentner News";
			}

			AlarmAddDto alarmAddDto = new AlarmAddDto();
			alarmAddDto.setAlarmSj(msg);
			alarmAddDto.setAlarmCn(bbsAddDto.getBbsSj());
			alarmAddDto.setAlarmSe("C");
			alarmAddDto.setAlarmUrl(bbsAddDto.getBbsNo().toString());
			commonService.postAllAlarm(alarmAddDto);

			PushDto push = new PushDto();
			push.setBody(bbsAddDto.getBbsSj());
			commonService.postAllFCMPush(push, "/help/notice");
		}
		// 알림 메세지 발송
		return result;
	}

	public int putBbs(Integer bbsNo, BbsAddDto bbsAddDto) {
		bbsAddDto.setBbsNo(bbsNo);
		bbsAddDto.setRegisterNo(SecurityUtil.getMemberNo());
		return adminBbsMapper.updateBbs(bbsAddDto);
	}

	public int deleteBbs(String bbsNoArr) {
		return adminBbsMapper.deleteBbs(bbsNoArr);
	}

	public int putFixBbs(String bbsNoArr, String type, String bbsTp) throws Exception{
		if("Y".equals(type)){
			int cnt = adminBbsMapper.selectFixBbs(bbsTp);
			if(cnt > 2){
				throw new Exception("3개 이상 고정할 수 없습니다.");
			}
		}
		return adminBbsMapper.updateFixBbs(bbsNoArr, type);
	}
}
