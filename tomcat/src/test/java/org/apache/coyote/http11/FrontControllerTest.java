package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FrontControllerTest {

    private final FrontController frontController = new FrontController();

    @DisplayName("/ 요청이 올 경우 Hello world가 담긴 HttpResponse를 반환한다.")
    @Test
    void nonStaticFileBasicRequest() {
        FrontController frontController = new FrontController();
        HttpRequest httpRequest = new HttpRequest("GET / HTTP/1.1", List.of());
        HttpResponse httpResponse = frontController.performRequest(httpRequest);

        String expectedResponseBody = "Hello world!";

        assertAll(
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.OK),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.CONTENT_TYPE)).isEqualTo(
                        FileExtension.HTML.getContentType()),
                () -> assertThat(httpResponse.getHeader(HttpHeaderType.CONTENT_LENGTH)).isEqualTo(
                        String.valueOf(expectedResponseBody.getBytes().length)),
                () -> assertThat(httpResponse.getResponseBody()).isEqualTo(expectedResponseBody)
        );
    }

    @DisplayName("/login 요청이 올 경우 login.html파일이 담긴 HttpResponse를 반환한다.")
    @Test
    void nonStaticFileLoginRequest() throws IOException {
        HttpRequest httpRequest = new HttpRequest("GET /login?account=rex&password=password HTTP/1.1", List.of());
        HttpResponse httpResponse = frontController.performRequest(httpRequest);

        assertAll(
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND)
        );
    }

    @DisplayName("잘못된 QueryParameter로 로그인 요청이 올 경우 경우 Not Found 응답을 한다.")
    @Test
    void responseBadRequestWhenRequestInvalidLoginParam() {
        HttpRequest httpRequest = new HttpRequest("GET /login?account=rex HTTP/1.1", List.of());
        HttpResponse httpResponse = frontController.performRequest(httpRequest);

        assertAll(
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
        );
    }

    @DisplayName("처리할 수 없는 요청이 올 경우 경우 Not Found 응답을 한다.")
    @Test
    void responseBadRequestWhenRequestThatCannotBePerform() {
        HttpRequest httpRequest = new HttpRequest("GET /wrongUrl HTTP/1.1", List.of());
        HttpResponse httpResponse = frontController.performRequest(httpRequest);

        assertAll(
                () -> assertThat(httpResponse.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
        );
    }
}
