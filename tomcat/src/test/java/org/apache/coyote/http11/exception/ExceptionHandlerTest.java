package org.apache.coyote.http11.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.http11.message.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExceptionHandlerTest {

    private final ExceptionHandler exceptionHandler = new ExceptionHandler();

    @DisplayName("NotFoundException을 받으면 404 응답을 반환한다.")
    @Test
    void handle_returns404Response_forNotFoundException() throws IOException {
        // given
        NotFoundException exception = new NotFoundException();
        String expectedBody = readPageContent("static/404.html");

        // when
        HttpResponse response = exceptionHandler.handle(exception);

        // then
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode()),
                () -> assertThat(response.getBody().toText()).isEqualTo(expectedBody),
                () -> assertThat(response.getHeaders().get("Content-Type").getFirst()).isEqualTo(
                        "text/html;charset=utf-8"),
                () -> assertThat(response.getHeaders().get("Content-Length").getFirst()).isEqualTo(
                        String.valueOf(expectedBody.getBytes().length))
        );
    }

    @DisplayName("일반 예외를 받으면 500 응답을 반환한다.")
    @Test
    void handle_returns500Response_forGenericException() throws IOException {
        // given
        RuntimeException exception = new RuntimeException("Something bad happened");
        String expectedBody = readPageContent("static/500.html");

        // when
        HttpResponse response = exceptionHandler.handle(exception);

        // then
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getCode()),
                () -> assertThat(response.getBody().toText()).isEqualTo(expectedBody),
                () -> assertThat(response.getHeaders().get("Content-Type").getFirst()).isEqualTo(
                        "text/html;charset=utf-8"),
                () -> assertThat(response.getHeaders().get("Content-Length").getFirst()).isEqualTo(
                        String.valueOf(expectedBody.getBytes().length))
        );
    }

    private String readPageContent(String pagePath) throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(pagePath)) {
            if (is == null) {
                throw new IOException("Resource not found: " + pagePath);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}