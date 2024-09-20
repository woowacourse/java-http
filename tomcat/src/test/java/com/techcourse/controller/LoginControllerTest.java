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

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.TestFixtures.*;

class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    private void handleRequest(HttpRequest httpRequest, String[] expectedResponses) throws Exception {
        HttpResponse httpResponse = createHttpResponse(httpRequest);

        for (String expectedResponse : expectedResponses) {
            assertThat(new String(ResponseFormatter.toResponseFormat(httpResponse), StandardCharsets.UTF_8)).contains(expectedResponse);
        }
    }

    private HttpResponse createHttpResponse(HttpRequest httpRequest) throws Exception {
        HttpResponse httpResponse = new HttpResponse();
        loginController.service(httpRequest, httpResponse);
        return httpResponse;
    }

    @Test
    @DisplayName("GET /login을 요청하면 /static/login.html 페이지로 리다이렉트한다.")
    void login_GET() throws Exception {
        // given
        final HttpRequest httpRequest = buildHttpRequest(GET, "/login", "");

        // when, then
        handleRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /login.html"});
    }

    @Test
    @DisplayName("/login 주소에서 정확한 계정명과 비밀번호로 로그인에 성공하면 /index.html 페이지로 리다이렉트한다.")
    void login_POST_success() throws Exception {
        // given
        String requestBody = "account=gugu&password=password";
        final HttpRequest httpRequest = buildHttpRequest(POST, "/login", requestBody);

        // when, then
        handleRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /index.html"});
    }

    @Test
    @DisplayName("/login 주소에서 정확한 계정명과 비밀번호로 로그인에 성공하면 Set-Cookie를 반환한다.")
    void login_POST_success_returnSetCookie() throws Exception {
        // given
        String requestBody = "account=gugu&password=password";
        final HttpRequest httpRequest = buildHttpRequest(POST, "/login", requestBody);

        // when, then
        handleRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Set-Cookie: JSESSIONID="});
    }

    @Test
    @DisplayName("/login 주소에서 정확한 계정명과 비밀번호로 로그인에 성공한 뒤 /login 주소로 접근하면 /index.html 페이지로 리다이렉트한다.")
    void login_POST_success_then_GET_withValidSessionId() throws Exception {
        // given
        String requestBody = "account=gugu&password=password";
        final HttpRequest postLoginRequest = buildHttpRequest(POST, "/login", requestBody);

        HttpResponse response = createHttpResponse(postLoginRequest);
        String sessionId = extractSessionIdFromResponse(response.getResponseHeader().get("Set-Cookie"));

        final String rawGetLoginRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                "");

        final HttpRequest getLoginRequest = new HttpRequest(new ByteArrayInputStream(rawGetLoginRequest.getBytes(StandardCharsets.UTF_8)));

        //when, then
        handleRequest(getLoginRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /index.html"});
    }

    private String extractSessionIdFromResponse(String response) {
        Pattern pattern = Pattern.compile("JSESSIONID=([^;]+)");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalStateException("JSESSIONID가 올바르게 설정되지 않았습니다.");
    }

    @Test
    @DisplayName("/login 주소에서 정확한 계정명과 비밀번호로 로그인에 성공한 뒤 유효하지 않은 쿠키 정보로 /login 주소에 접근하면 /login.html 페이지를 응답한다.")
    void login_POST_then_GET_withInvalidSessionId() throws Exception {
        // given
        String requestBody = "account=gugu&password=password";
        final HttpRequest postLoginRequest = buildHttpRequest(POST, "/login", requestBody);

        createHttpResponse(postLoginRequest);

        String cookie = "Cookie: JSESSIONID=asdf";
        final String rawGetLoginRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                cookie + " ",
                "",
                "");

        final HttpRequest getLoginRequest = new HttpRequest(new ByteArrayInputStream(rawGetLoginRequest.getBytes(StandardCharsets.UTF_8)));


        // when, then
        handleRequest(getLoginRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /login.html"});
    }

    @CsvSource({"account=chorong&password=password", "account=gugu&password=sosad", "account= &password=password", "account=gugu", "password=password"})
    @EmptySource
    @ParameterizedTest
    @DisplayName("/login 주소에서 부정확한 계정명이나 비밀번호로 로그인을 시도하면 /401.html 페이지로 리다이렉트한다.")
    void login_POST_fail_invalidAccount(String requestBody) throws Exception {
        // given
        final HttpRequest postLoginRequest = buildHttpRequest(POST, "/login", requestBody);

        // when, then
        handleRequest(postLoginRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /401.html"});
    }
}
