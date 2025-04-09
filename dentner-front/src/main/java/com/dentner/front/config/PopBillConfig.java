package com.dentner.front.config;

import com.popbill.api.AccountCheckService;
import com.popbill.api.KakaoService;
import com.popbill.api.MessageService;
import com.popbill.api.accountcheck.AccountCheckServiceImp;
import com.popbill.api.kakao.KakaoServiceImp;
import com.popbill.api.message.MessageServiceImp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PopBillConfig {
	
	@Value("${popbill.linkId}")
	private String linkID;

	@Value("${popbill.secretKey}")
	private String secretKey;

	@Value("${popbill.isTest}")
	private boolean isTest;

	@Value("${popbill.isIpRestrictOnOff}")
	private boolean isIPRestrictOnOff;

	@Value("${popbill.useStaticIp}")
	private boolean useStaticIP;

	@Value("${popbill.useLocalTimeYn}")
	private boolean useLocalTimeYN;
	
	@Bean(name="AccountCheckService")
	AccountCheckService accountCheckService(){
		
		AccountCheckServiceImp service = new AccountCheckServiceImp();
		service.setLinkID(linkID);
		service.setSecretKey(secretKey);
		service.setTest(isTest);
		service.setIPRestrictOnOff(isIPRestrictOnOff);
		service.setUseStaticIP(useStaticIP);
		service.setUseLocalTimeYN(useLocalTimeYN);
      	return service;
	}
	
	@Bean(name="MessageService")
	MessageService messageService(){
		
		MessageServiceImp service = new MessageServiceImp();
		service.setLinkID(linkID);
		service.setSecretKey(secretKey);
		service.setTest(isTest);
		service.setIPRestrictOnOff(isIPRestrictOnOff);
		service.setUseStaticIP(useStaticIP);
		service.setUseLocalTimeYN(useLocalTimeYN);
		return service;
	}

	@Bean(name="KakaoService")
	KakaoService kakaoService(){

		KakaoServiceImp service = new KakaoServiceImp();
		service.setLinkID(linkID);
		service.setSecretKey(secretKey);
		service.setTest(isTest);
		service.setIPRestrictOnOff(isIPRestrictOnOff);
		service.setUseStaticIP(useStaticIP);
		service.setUseLocalTimeYN(useLocalTimeYN);
		return service;
	}
  
}
