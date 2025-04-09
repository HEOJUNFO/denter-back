package com.dentner.core.cmmn.service.response;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestControllerAdvice(basePackages = {"com.dentner.admin", "com.dentner.front", "com.dentner.core.config"})
public class ResponseAdvice implements ResponseBodyAdvice<Object> {

    /**
     * CodeOceanResponse -> beforeBodyWrite 예외
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
    	Type type = returnType.getGenericParameterType();

        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type rawType = parameterizedType.getRawType();
            if (rawType.equals(ResponseEntity.class)) {
            	return false;
//                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
//                if (actualTypeArguments.length == 1 && CodeOceanResponse.class.isAssignableFrom((Class<?>) actualTypeArguments[0])) {
//                    return false;
//                }
            }
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        HttpServletRequest servletRequest = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String message = (String) servletRequest.getAttribute("responseMessage");

        int status = servletResponse.getStatus();
        HttpStatus resolve = HttpStatus.resolve(status);

        if (resolve == null) {
            return body;
        }

        if (resolve.is2xxSuccessful()) {
            if (MediaType.TEXT_PLAIN.equals(selectedContentType)) {
                try {
                    /**
                     1.먼저 Controller에서 String 타입으로 반환.
                     2.@ResponseBody로 반환하기 때문에 ReturnValueHandler를 거칠 때 MessageConverter를 호출.
                     3.여기서 StringHttpMessageConverter가 선택.
                     4.ResponseBodyAdvice가 Controller의 반환값(문자열)을 가로채서 ApiResponse로 감싼다.
                     5.HandlerAdapter로 ModelAndView를 반환하는 과정에서 타입 캐스팅 에러가 발생.
                     HTTP Body에 문자열 데이터를 write() 하는 과정에서 addDefaultHeaders() 를 호출한다.
                     StringHttpMessageConverter의 addDefaultHeaders() 의 인자에는 String 타입의 데이터가 들어가는데, 현재 ResponseBodyAdvice 때문에 데이터가 ApiResponse 타입.
                     따라서 ApiResponse 타입이 String으로 변환될 수 없으므로 ClassCastException이 발생.
                     참고.https://medium.com/@qili7479/handle-springboot-responsebodyadvice-error-xxx-cannot-be-cast-to-java-lang-string-e64fad76d56e
                     */
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                    return new ObjectMapper().writeValueAsString(CodeOceanResponse.response(status, message, body));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    return e.getMessage();
                }
            } else {
                return CodeOceanResponse.response(status, message, body);
            }
        } else {
            return CodeOceanErrorResponse.response(status, "Error occurred", body);
        }
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<CodeOceanResponse<Object>> handleException(BadCredentialsException e) {
        e.printStackTrace();

        CodeOceanResponse<Object> response = CodeOceanResponse.response(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CodeOceanResponse<Object>> handleException(Exception e) {
        e.printStackTrace();

        CodeOceanResponse<Object> response = CodeOceanResponse.response(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
