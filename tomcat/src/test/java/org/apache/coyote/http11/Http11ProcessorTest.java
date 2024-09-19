package org.apache.coyote.http11;

import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_TYPE_HTML = "Content-Type: text/html;charset=utf-8";
    private static final String CONTENT_TYPE_CSS = "Content-Type: text/css";
    private static final String CONTENT_TYPE_JAVASCRIPT = "Content-Type: application/javascript";
    private static final String CONTENT_TYPE_SVG = "Content-Type: image/svg+xml";
    private static final String POST = "POST";
    private static final String GET = "GET";

    private void processRequest(String httpRequest, String[] expectedResponses) {
        String response = createResponse(httpRequest);
        for (String expectedResponse : expectedResponses) {
            assertThat(response).contains(expectedResponse);
        }
    }

    private String createResponse(String httpRequest) {
        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        processor.process(socket);

        return socket.output();
    }

    private String buildHttpRequest(String method, String path, String body) throws IOException {
        String requestLine = String.join(" ",
                method,
                path,
                "HTTP/1.1");

        return String.join("\r\n",
                requestLine,
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "Connection: keep-alive",
                "",
                body);
    }

    private void testResourceResponse(String requestPath, String contentType, String expectedFilePath) throws IOException {
        // given
        final String httpRequest = buildHttpRequest(GET, requestPath, "");

        final URL resource = getClass().getClassLoader().getResource(expectedFilePath);
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String contentLength = "Content-Length: " + responseBody.length;
        String body = new String(responseBody);

        // when, then
        processRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.OK.getStatus(), contentType, contentLength, body});
    }

    private static Stream<Arguments> resourceProvider() {
        return Stream.of(
                Arguments.of("/", CONTENT_TYPE_HTML, "static/index.html"),
                Arguments.of("/css/styles.css", CONTENT_TYPE_CSS, "static/css/styles.css"),
                Arguments.of("/js/scripts.js", CONTENT_TYPE_JAVASCRIPT, "static/js/scripts.js"),
                Arguments.of("/assets/img/error-404-monochrome.svg", CONTENT_TYPE_SVG, "static/assets/img/error-404-monochrome.svg"),
                Arguments.of("/index.html", CONTENT_TYPE_HTML, "static/index.html"),
                Arguments.of("/register.html", CONTENT_TYPE_HTML, "static/register.html"),
                Arguments.of("/login.html", CONTENT_TYPE_HTML, "static/login.html")
        );
    }

    @ParameterizedTest
    @MethodSource("resourceProvider")
    @DisplayName("정적 자원 요청에 대한 응답을 검증한다.")
    void resourceResponse(String requestPath, String contentType, String expectedFilePath) throws IOException {
        testResourceResponse(requestPath, contentType, expectedFilePath);
    }

    @Test
    @DisplayName("request 처리 중 IllegalArgumentException이 발생하면 /static/400.html 페이지로 리다이렉트한다.")
    void failProcess400() {
        // given
        final String httpRequest = String.join("\r\n",
                "ERROR",
                "Host: localhost:8080",
                "");

        // when, then
        processRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /400.html"});
    }

    @Test
    @DisplayName("request 처리 중 IOException이 발생하면 /static/500.html 페이지로 리다이렉트한다.")
    void processRequest_throwsIOException_returns500() {
        // given
        StubSocket socket = new StubSocket("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n") {
            @Override
            public InputStream getInputStream() throws IOException {
                throw new IOException("Test IOException");
            }
        };
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String response = socket.output();
        String status = HttpStatus.FOUND.getStatus();
        String location = "Location: /500.html";

        assertThat(response).contains(status, CONTENT_TYPE_HTML, location);
    }

    @Test
    @DisplayName("존재하지 않는 주소에 접근하면 /static/404.html 페이지로 리다이렉트한다.")
    void notFoundPage() throws IOException {
        // given
        final String httpRequest = buildHttpRequest(GET, "/fake", "");

        // when, then
        processRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /404.html"});
    }

    @Test
    @DisplayName("존재하지 않는 정적 파일 주소에 접근하면 /static/404.html 페이지로 리다이렉트한다.")
    void notFoundPage_staticIndex() throws IOException {
        // given
        final String httpRequest = buildHttpRequest(GET, "/fake.html", "");

        // when, then
        processRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /404.html"});
    }

    @Test
    @DisplayName("/login 주소에서 정확한 계정명과 비밀번호로 로그인에 성공하면 /index.html 페이지로 리다이렉트한다.")
    void login_success() throws IOException {
        // given
        String requestBody = "account=gugu&password=password";
        final String httpRequest = buildHttpRequest(POST, "/login", requestBody);

        // when, then
        processRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /index.html"});
    }

    @Test
    @DisplayName("/login 주소에서 정확한 계정명과 비밀번호로 로그인에 성공하면 Set-Cookie를 반환한다.")
    void login_success_returnSetCookie() throws IOException {
        // given
        String requestBody = "account=gugu&password=password";
        final String httpRequest = buildHttpRequest(POST, "/login", requestBody);

        // when, then
        processRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Set-Cookie: JSESSIONID="});
    }

    @Test
    @DisplayName("/login 주소에서 정확한 계정명과 비밀번호로 로그인에 성공한 뒤 /login 주소로 접근하면 /index.html 페이지로 리다이렉트한다.")
    void login_success_then_accessLoginPage() throws IOException {
        // given
        String requestBody = "account=gugu&password=password";
        final String postLoginRequest = buildHttpRequest(POST, "/login", requestBody);

        String response = createResponse(postLoginRequest);
        String sessionId = extractSessionIdFromResponse(response);

        final String getLoginRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                "");

        //when, then
        processRequest(getLoginRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /index.html"});
    }

    private String extractSessionIdFromResponse(String response) {
        Pattern pattern = Pattern.compile("Set-Cookie: JSESSIONID=([^;]+)");
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalStateException("JSESSIONID가 올바르게 설정되지 않았습니다.");
    }

    @Test
    @DisplayName("/login 주소에서 정확한 계정명과 비밀번호로 로그인에 성공한 뒤 유효하지 않은 쿠키 정보로 /login 주소에 접근하면 /login.html 페이지를 응답한다.")
    void login_invalidSessionId() throws IOException {
        // given
        String requestBody = "account=gugu&password=password";
        final String postLoginRequest = buildHttpRequest(POST, "/login", requestBody);

        createResponse(postLoginRequest);

        String cookie = "Cookie: JSESSIONID=asdf";
        final String getLoginRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                cookie + " ",
                "",
                "");

        // when, then
        processRequest(getLoginRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /login.html"});
    }

    @CsvSource({"account=chorong&password=password", "account=gugu&password=sosad", "account= &password=password", "account=gugu", "password=password"})
    @EmptySource
    @ParameterizedTest
    @DisplayName("/login 주소에서 부정확한 계정명이나 비밀번호로 로그인을 시도하면 /401.html 페이지로 리다이렉트한다.")
    void login_fail_invalidAccount(String requestBody) throws IOException {
        // given
        final String postLoginRequest = buildHttpRequest(POST, "/login", requestBody);

        // when, then
        processRequest(postLoginRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /401.html"});
    }

    @Test
    @DisplayName("/register 주소에서 정확한 계정명과 이메일, 비밀번호로 회원가입에 성공하면 /index.html 페이지로 리다이렉트한다.")
    void register_success() throws IOException {
        // given
        String requestBody = "account=ash&password=ashPassword&email=test@email.com";
        final String httpRequest = buildHttpRequest(POST, "/register", requestBody);

        // when, then
        processRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /index.html"});
    }

    @Test
    @DisplayName("/register 주소에서 이미 존재하는 계정명으로 회원가입을 시도하면 /register 페이지로 리다이렉트한다.")
    void register_fail_accountAlreadyExists() throws IOException {
        // given
        String requestBody = "account=gugu&password=newpassword&email=test@email.com";
        final String httpRequest = buildHttpRequest(POST, "/register", requestBody);

        // when, then
        processRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /register.html"});
    }

    @CsvSource({"account=chorong&password=password", "account=gugu&email=test@email.com", "account= "})
    @EmptySource
    @ParameterizedTest
    @DisplayName("/register 주소에서 필수 입력값을 누락하고 회원가입을 시도하면 /register 페이지로 리다이렉트한다.")
    void register_fail_missingEssentialValues(String requestBody) throws IOException {
        // given
        final String httpRequest = buildHttpRequest(POST, "/register", requestBody);

        // when, then
        processRequest(httpRequest, new String[]{HTTP_VERSION + " " + HttpStatus.FOUND.getStatus(), CONTENT_TYPE_HTML, "Location: /register.html"});
    }
}
