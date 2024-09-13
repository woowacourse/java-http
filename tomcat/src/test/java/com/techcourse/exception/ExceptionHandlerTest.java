package com.techcourse.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.exception.handler.ExceptionHandler;
import com.techcourse.exception.handler.GlobalExceptionHandler;
import java.util.Map;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExceptionHandlerTest {

    private final ExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @DisplayName("UnauthorizedException에 대해 응답에 401 리다이렉션을 추가한다.")
    @Test
    void handleUnauthorizedException() {
        HttpResponse response = new HttpResponse();
        exceptionHandler.handle(new UnauthorizedException("test"), response);

        HttpResponse expectedResponse = new HttpResponse(
                HttpStatus.FOUND,
                Map.of(HttpHeader.LOCATION.getName(), "/401.html")
        );
        assertThat(response).isEqualTo(expectedResponse);
    }

    @DisplayName("IllegalArgumentException을 올바르게 처리한다.")
    @Test
    void handleIllegalArgumentException() {
        String message = "test";
        HttpResponse response = new HttpResponse();
        exceptionHandler.handle(new IllegalArgumentException(message), response);

        HttpResponse expectedResponse = new HttpResponse(
                HttpStatus.BAD_REQUEST,
                Map.of(
                        HttpHeader.CONTENT_TYPE.getName(), "text/plain;charset=utf-8 ",
                        HttpHeader.CONTENT_LENGTH.getName(), String.valueOf(message.getBytes().length)
                ),
                message
        );
        assertThat(response).isEqualTo(expectedResponse);
    }

    @DisplayName("Exception을 올바르게 처리한다.")
    @Test
    void handleException() {
        String message = "서버에 문제가 발생했습니다.";
        HttpResponse response = new HttpResponse();
        exceptionHandler.handle(new Exception(), response);

        HttpResponse expectedResponse = new HttpResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                Map.of(
                        HttpHeader.CONTENT_TYPE.getName(), "text/plain;charset=utf-8 ",
                        HttpHeader.CONTENT_LENGTH.getName(), String.valueOf(message.getBytes().length)
                ),
                message
        );
        assertThat(response).isEqualTo(expectedResponse);
    }
}
