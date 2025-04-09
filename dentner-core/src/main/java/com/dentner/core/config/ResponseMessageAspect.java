package com.dentner.core.config;

import com.dentner.core.cmmn.service.response.ResponseMessage;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class ResponseMessageAspect {

	@AfterReturning("@annotation(responseMessage)")
    public void addCustomMessageToRequest(ResponseMessage responseMessage) {
        String message = responseMessage.value();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        attributes.getRequest().setAttribute("responseMessage", message);
    }
}
