package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("로그인 컨트롤러 테스트")
class PostLoginControllerTest {

    private PostLoginController postLoginController;

    @BeforeEach
    void setUp() {
        postLoginController = new PostLoginController();
    }

    @DisplayName("로그인 요청일 경우, 로그인을 시도한다.")
    @Test
    void login() throws IOException {
        // given
        String body = buildRequestBody(Map.of("account", "gugu", "password", "password"));
        HttpRequest request = buildHttpRequest("POST", "/login", body);

        // when
        HttpResponse httpResponse = postLoginController.run(request);

        // then
        String expectedRequestLine = "HTTP/1.1 " + HttpStatusCode.FOUND.toStatus();
        String expectedLocationHeader = "Location: " + "/index.html";
        String expectedContentType = "Content-Type: " + MimeType.HTML.getContentType();

        assertAll(
                () -> assertThat(httpResponse.toByte()).contains(expectedRequestLine.getBytes()),
                () -> assertThat(httpResponse.toByte()).contains(expectedLocationHeader.getBytes()),
                () -> assertThat(httpResponse.toByte()).contains(expectedContentType.getBytes())
        );
    }

    private String buildRequestBody(Map<String, String> parameters) {
        return parameters.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .reduce((param1, param2) -> param1 + "&" + param2)
                .orElse("");
    }

    private HttpRequest buildHttpRequest(String method, String path, String body) throws IOException {
        String requestLine = String.join(" ", method, path, "HTTP/1.1");

        String httpRequest = String.join("\r\n",
                requestLine,
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "Connection: keep-alive",
                "",
                body);

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(StandardCharsets.UTF_8));
        return new HttpRequest(inputStream);
    }

    @DisplayName("로그인에 실패할 경우, 401페이지로 리다이렉트한다.")
    @Test
    void failLogin() throws IOException {
        // given
        String body = buildRequestBody(Map.of("account", "gugu", "password", "invalidPassword"));
        HttpRequest request = buildHttpRequest("POST", "/login", body);

        // when
        HttpResponse httpResponse = postLoginController.run(request);

        // then
        String expectedRequestLine = "HTTP/1.1 " + HttpStatusCode.FOUND.toStatus();
        String expectedLocationHeader = "Location: " + "/401.html";
        String expectedContentType = "Content-Type: " + MimeType.HTML.getContentType();

        assertAll(
                () -> assertThat(httpResponse.toByte()).contains(expectedRequestLine.getBytes()),
                () -> assertThat(httpResponse.toByte()).contains(expectedLocationHeader.getBytes()),
                () -> assertThat(httpResponse.toByte()).contains(expectedContentType.getBytes())
        );
    }
}
