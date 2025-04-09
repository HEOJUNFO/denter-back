package com.dentner.core.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.dentner.core.cmmn.exception.TokenValidationException;
import com.dentner.core.cmmn.service.response.CodeOceanResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		if (isSkippablePath(requestURI)) {
			//Authentication authentication = new PreAuthenticatedAuthenticationToken("anonymous", null, AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"));
	        //SecurityContextHolder.getContext().setAuthentication(authentication);
	        filterChain.doFilter(request, response);
	        return;
	    }
		
        String token = jwtTokenProvider.resolveAccessToken((HttpServletRequest) request);
        
        if (token == null) {
            sendErrorResponse(response, HttpStatus.FORBIDDEN, "Empty JWT token");
            return;
        }
        
        try {
        	if (jwtTokenProvider.validateToken(token, (HttpServletResponse) response)) {
        		// 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
        		Authentication authentication = jwtTokenProvider.getAuthentication(token);
        		SecurityContextHolder.getContext().setAuthentication(authentication);
        	}
        } catch (TokenValidationException e) {
        	sendErrorResponse(response, e.getStatus(), e.getMessage());
            return;
        }
        
        filterChain.doFilter(request, response);
	}
	
	private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        CodeOceanResponse<Object> errorResponse = CodeOceanResponse.response(status.value(), message);
        new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
    }
	
	private boolean isSkippablePath(String requestURI) {
	    List<String> skippablePaths = Arrays.asList(
	            "/api/v1/join/local/member",
				"/api/v1/join/country/member",
				"/api/v1/join/email",
				"/api/v1/join/terms",
				"/api/v1/join/password",
				"/api/v1/join/dup/nickname",
				"/api/v1/join/dup/email",
				"/api/v1/login/social",
				"/api/v1/login",
				"/api/v1/main/banner",
				"/api/v1/main/stat",
				"/api/v1/main/bbs",
				"/api/v1/admin/login",
				"/api/v1/common/auth-phone",
				"/api/v1/reset-password",
				"/api/v1/admin/apply/excel",
				"/api/v1/common/code",
				"/api/v1/common/teeth-type",
				"/api/v1/common/status",
				"/api/v1/common/test",
				"/api/v1/common/stat-cnt",
				"/api/v1/common/account",
				"/api/v1/join/dup/nickname",
				"/api/v1/join/dup/email",
				"/api/v1/join/member",
				"/api/v1/bbs",
				"/test/",
				"/ws",
	            "/v3/api-docs",
	            "/swagger-ui.html",
	            "/swagger-ui/"
	    );

	    return skippablePaths.stream().anyMatch(requestURI::startsWith);
	}
}
