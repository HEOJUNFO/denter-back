package com.dentner.core.cmmn.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CodeOceanResponse<T> {

	private int statusCode;
    private String message;
    private T data;
    
    public CodeOceanResponse(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = null; 
    }
    
	/**
	 * 데이터 없이 응답 객체를 생성하기 위한 정적 메소드입니다.
	 * 
	 * @param <T> 응답에 있는 데이터(있을 경우)의 타입입니다.
	 * @param statusCode 응답의 HTTP 상태 코드입니다.
	 * @param responseMessage 응답과 관련된 메시지입니다.
	 * @return 데이터가 없는 새로운 {@link CodeOceanResponse} 인스턴스를 반환합니다.
	 */
    public static <T> CodeOceanResponse<T> response(final int statusCode, final String message) {
        return response(statusCode, message, null);
    }
    
    /**
     * 데이터를 선택적으로 포함하여 응답 객체를 생성하는 정적 메소드입니다.
     * 
     * @param <T> 응답에서 포함되는 데이터의 타입입니다.
     * @param statusCode 응답의 HTTP 상태 코드입니다.
     * @param responseMessage 응답과 관련된 메시지입니다.
     * @param t 응답에 포함될 데이터입니다. null일 수 있습니다.
     * @return 데이터를 포함할 수 있는 새로운 {@link CodeOceanResponse} 인스턴스를 반환합니다.
     */
    public static <T> CodeOceanResponse<T> response(final int statusCode, final String message, final T t) {
        return CodeOceanResponse.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .data(t)
                .build();
    }
    
}
