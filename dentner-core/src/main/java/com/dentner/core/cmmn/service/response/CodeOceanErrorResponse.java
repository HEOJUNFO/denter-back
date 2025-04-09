package com.dentner.core.cmmn.service.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CodeOceanErrorResponse<T> {

	private int statusCode;
    private String message;
    private T error;
    
    /**
     * 데이터 없이 오류 응답 객체를 생성하는 생성자입니다.
     *
     * @param statusCode HTTP 상태 코드입니다.
     * @param responseMessage 응답과 관련된 메시지입니다.
     */
    public CodeOceanErrorResponse(final int statusCode, final String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.error = null; // 명시적으로 오류 데이터를 null로 설정
    }

    /**
     * 데이터 없이 오류 응답 객체를 생성하는 정적 메소드입니다.
     *
     * @param <T> 오류 데이터의 타입입니다.
     * @param statusCode HTTP 상태 코드입니다.
     * @param responseMessage 응답과 관련된 메시지입니다.
     * @return 데이터 없이 생성된 {@link CodeOceanErrorResponse} 인스턴스입니다.
     */
    public static <T> CodeOceanErrorResponse<T> response(final int statusCode, final String message) {
        return response(statusCode, message, null);
    }

    /**
     * 오류 데이터를 포함하여 오류 응답 객체를 생성하는 정적 메소드입니다.
     *
     * @param <T> 오류 데이터의 타입입니다.
     * @param statusCode HTTP 상태 코드입니다.
     * @param responseMessage 응답과 관련된 메시지입니다.
     * @param error 오류 데이터입니다. null일 수 있습니다.
     * @return 생성된 {@link CodeOceanErrorResponse} 인스턴스입니다.
     */
    public static <T> CodeOceanErrorResponse<T> response(final int statusCode, final String message, final T error) {
        return CodeOceanErrorResponse.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .error(error)
                .build();
    }
}
