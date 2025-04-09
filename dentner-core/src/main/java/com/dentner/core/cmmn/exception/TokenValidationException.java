package com.dentner.core.cmmn.exception;

import org.springframework.http.HttpStatus;

public class TokenValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private HttpStatus status;
    private String message;
    
    public TokenValidationException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
