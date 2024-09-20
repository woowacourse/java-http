package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.TestFixtures.*;

class RegisterControllerTest {

    private final RegisterController registerController = new RegisterController();

    private void handleRequest(HttpRequest httpRequest, String[] expectedResponses) throws Exception {
        HttpResponse httpResponse = new HttpResponse();

        registerController.service(httpRequest, httpResponse);
        for (String expectedResponse : expectedResponses) {
            assertThat(new String(ResponseFormatter.toResponseFormat(httpResponse), StandardCharsets.UTF_8)).contains(expectedResponse);
        }
    }

    @Test
    @DisplayName("GET /register를 요청하면 /static/register.html 페이지로 리다이렉트한다.")
    void register_GET() throws Exception {
        // given
        final HttpRequest httpRequest = buildHttpRequest(GET, "/register", "");

        // when, then
        handleRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /register.html"});
    }

    @Test
    @DisplayName("/register 주소에서 정확한 계정명과 이메일, 비밀번호로 회원가입에 성공하면 /index.html 페이지로 리다이렉트한다.")
    void register_POST_success() throws Exception {
        // given
        String requestBody = "account=ash&password=ashPassword&email=test@email.com";
        final HttpRequest httpRequest = buildHttpRequest(POST, "/register", requestBody);

        // when, then
        handleRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /index.html"});
    }

    @Test
    @DisplayName("/register 주소에서 이미 존재하는 계정명으로 회원가입을 시도하면 /register 페이지로 리다이렉트한다.")
    void register_POST_fail_accountAlreadyExists() throws Exception {
        // given
        String requestBody = "account=gugu&password=newpassword&email=test@email.com";
        final HttpRequest httpRequest = buildHttpRequest(POST, "/register", requestBody);

        // when, then
        handleRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /register.html"});
    }

    @CsvSource({"account=chorong&password=password", "account=gugu&email=test@email.com", "account= "})
    @EmptySource
    @ParameterizedTest
    @DisplayName("/register 주소에서 필수 입력값을 누락하고 회원가입을 시도하면 /register 페이지로 리다이렉트한다.")
    void register_POST_fail_missingEssentialValues(String requestBody) throws Exception {
        // given
        final HttpRequest httpRequest = buildHttpRequest(POST, "/register", requestBody);

        // when, then
        handleRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /register.html"});
    }
}
