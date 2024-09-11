package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.HttpStatusCode;
import org.apache.coyote.MimeType;
import org.apache.coyote.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("회원가입 컨트롤러 테스트")
class RegisterControllerTest {

    private RegisterController registerController;

    @BeforeEach
    void setUp() {
        registerController = new RegisterController();
    }

    @DisplayName("회원가입에 성공할 경우, index.html로 리다이렉트한다.")
    @Test
    void signup() throws IOException {
        // given
        String path = "/register";
        String queryKey1 = "account";
        String queryValue1 = "gugu";
        String queryKey2 = "account";
        String queryValue2 = "gugu";
        String queryKey3 = "password";
        String queryValue3 = "password";

        String body = String.join("&",
                queryKey1 + "=" + queryValue1,
                queryKey2 + "=" + queryValue2,
                queryKey3 + "=" + queryValue3
        );

        String requestLine = String.join(" ", "POST", path, "HTTP/1.1");

        String httpRequest = String.join("\r\n",
                requestLine,
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "Connection: keep-alive",
                "",
                body);

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(StandardCharsets.UTF_8));
        HttpRequest request = new HttpRequest(inputStream);

        // when
        HttpResponse httpResponse = registerController.service(request, new SessionManager());

        // then
        String expectedRequestLine = "HTTP/1.1 " + HttpStatusCode.FOUND.toStatus();
        String expectedLocation = "Location: /index.html";
        String expectedContentType = "Content-Type: " + MimeType.HTML.getMimeType();

        assertAll(
                () -> assertThat(httpResponse.toByte()).contains(expectedRequestLine.getBytes()),
                () -> assertThat(httpResponse.toByte()).contains(expectedLocation.getBytes()),
                () -> assertThat(httpResponse.toByte()).contains(expectedContentType.getBytes())
        );
    }
}
