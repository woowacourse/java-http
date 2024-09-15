package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        String queryValue1 = "gugu1";
        String queryKey2 = "email";
        String queryValue2 = "gugu@email.com";
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
        HttpResponse response = new HttpResponse();

        // when
        registerController.service(request, response);

        // then
        String expectedRequestLine = "HTTP/1.1 " + HttpStatusCode.FOUND.toStatus();
        String expectedLocation = "Location: /index.html";
        String expectedContentType = "Content-Type: " + MimeType.HTML.getMimeType();

        assertAll(
                () -> assertThat(response.toByte()).contains(expectedRequestLine.getBytes()),
                () -> assertThat(response.toByte()).contains(expectedLocation.getBytes()),
                () -> assertThat(response.toByte()).contains(expectedContentType.getBytes())
        );
    }

    @DisplayName("회원가입에 성공할 경우, index.html로 리다이렉트한다.")
    @Test
    void testRegisterUser_Success() throws IOException {
        // given
        String body = buildRequestBody(Map.of(
                "account", "gugu",
                "email", "gugu@techcourse.com",
                "password", "password123"
        ));
        HttpRequest request = buildHttpRequest("POST", "/login", body);
        HttpResponse response = new HttpResponse();

        // when
        registerController.doPost(request, response);

        // then
        assertThat(response.getLocation()).isEqualTo("index.html");
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

    @DisplayName("account 정보가 없을 경우, IllegalArgumentException을 발생시킨다.")
    @Test
    void testValidateBody_AccountMissing() throws IOException {
        // given
        String body = buildRequestBody(Map.of(
                "email", "gugu@techcourse.com",
                "password", "password123"
        ));
        HttpRequest request = buildHttpRequest("POST", "/login", body);
        HttpResponse response = new HttpResponse();

        // when&then
        assertThatThrownBy(() -> registerController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("account가 존재하지 않습니다.");
    }

    @DisplayName("email 정보가 없을 경우, IllegalArgumentException을 발생시킨다.")
    @Test
    void testValidateBody_EmailMissing() throws IOException {
        // given
        String body = buildRequestBody(Map.of(
                "account", "gugu",
                "password", "password123"
        ));
        HttpRequest request = buildHttpRequest("POST", "/login", body);
        HttpResponse response = new HttpResponse();

        // when&then
        assertThatThrownBy(() -> registerController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("email가 존재하지 않습니다.");
    }

    @DisplayName("password 정보가 없을 경우, IllegalArgumentException을 발생시킨다.")
    @Test
    void testValidateBody_PasswordMissing() throws IOException {
        // given
        String body = buildRequestBody(Map.of(
                "account", "gugu",
                "email", "gugu@techcourse.com"
        ));
        HttpRequest request = buildHttpRequest("POST", "/login", body);
        HttpResponse response = new HttpResponse();

        assertThatThrownBy(() -> registerController.doPost(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("password가 존재하지 않습니다.");
    }
}
