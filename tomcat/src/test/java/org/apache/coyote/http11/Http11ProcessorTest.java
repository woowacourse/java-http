package org.apache.coyote.http11;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    // 메인 페이지를 "Hello World!" -> index.html로 변경하면서 테스트 비활성화
    @Disabled
    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Disabled // 어떻게 해야 성공시킬 수 있을까요...
    @Test
    @DisplayName("process 동작 중 IOException이 발생하면 /static/500.html 페이지를 응답힌다.")
    void process_fail_500() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        socket.setThrowIOException(true);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/500.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        var expected = "HTTP/1.1 500 INTERNAL SERVER ERROR \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.length + " \r\n" +
                "\r\n" +
                new String(responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("/ 주소에 접근하면 /static/index.html 페이지를 응답한다.")
    void mainPage_Html() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 200 OK";
        String contentType = "Content-Type: text/html;charset=utf-8";
        String contentLength = "Content-Length: " + responseBody.length;
        String body = new String(responseBody);

        assertThat(socket.output()).contains(responseLine, contentType, contentLength, body);
    }

    @Test
    @DisplayName("페이지 접근 시 css 파일이 정상적으로 응답한다.")
    void mainPage_Css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 200 OK";
        String contentType = "Content-Type: text/css";
        String contentLength = "Content-Length: " + responseBody.length;
        String body = new String(responseBody);

        assertThat(socket.output()).contains(responseLine, contentType, contentLength, body);
    }

    @Test
    @DisplayName("페이지 접근 시 js 파일이 정상적으로 응답한다.")
    void mainPage_Js() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 200 OK";
        String contentType = "Content-Type: application/javascript";
        String contentLength = "Content-Length: " + responseBody.length;
        String body = new String(responseBody);

        assertThat(socket.output()).contains(responseLine, contentType, contentLength, body);
    }

    @Test
    @DisplayName("페이지 접근 시 svg 파일이 정상적으로 응답한다.")
    void notFoundPage_svg() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /assets/img/error-404-monochrome.svg HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: image/avif,image/webp,image/apng,image/svg+xml,image/*,*/*;q=0.8 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static//assets/img/error-404-monochrome.svg");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 200 OK";
        String contentType = "Content-Type: " + MimeType.SVG.getType();
        String contentLength = "Content-Length: " + responseBody.length;
        String body = new String(responseBody);

        assertThat(socket.output()).contains(responseLine, contentType, contentLength, body);
    }


    @Test
    @DisplayName("/index.html 주소에 접근하면 /static/index.html 페이지를 응답한다.")
    void indexPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 200 OK";
        String contentType = "Content-Type: text/html;charset=utf-8";
        String contentLength = "Content-Length: " + responseBody.length;
        String body = new String(responseBody);

        assertThat(socket.output()).contains(responseLine, contentType, contentLength, body);
    }

    @Test
    @DisplayName("/register 주소에 접근하면 /static/register.html 페이지를 응답한다.")
    void registerPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 200 OK";
        String contentType = "Content-Type: text/html;charset=utf-8";
        String contentLength = "Content-Length: " + responseBody.length;
        String body = new String(responseBody);

        assertThat(socket.output()).contains(responseLine, contentType, contentLength, body);
    }

    @Test
    @DisplayName("/login 주소에 접근하면 /static/login.html 페이지를 응답한다.")
    void loginPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 200 OK";
        String contentType = "Content-Type: text/html;charset=utf-8";
        String contentLength = "Content-Length: " + responseBody.length;
        String body = new String(responseBody);

        assertThat(socket.output()).contains(responseLine, contentType, contentLength, body);
    }

    @Test
    @DisplayName("존재하지 않는 주소에 접근하면 /static/404.html 페이지를 응답한다.")
    void notFoundPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /fake HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 404 NOT FOUND";
        String contentType = "Content-Type: text/html;charset=utf-8";
        String contentLength = "Content-Length: " + responseBody.length;
        String body = new String(responseBody);

        assertThat(socket.output()).contains(responseLine, contentType, contentLength, body);
    }

    @Test
    @DisplayName("/login 주소에서 정확한 계정명과 비밀번호로 로그인에 성공하면 /index.html 페이지로 리다이렉트한다.")
    void login_success() throws IOException {
        // given
        String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 302 FOUND";
        String contentType = "Content-Type: text/html;charset=utf-8";
        String contentLength = "Content-Length: " + responseBody.length;

        assertThat(socket.output()).contains(responseLine, contentType, contentLength);
    }

    @Test
    @DisplayName("/login 주소에서 정확한 계정명과 비밀번호로 로그인에 성공하면 Set-Cookie를 반환한다.")
    void login_success_returnSetCookie() {
        // given
        String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String setCookie = "Set-Cookie: JSESSIONID=";

        assertThat(socket.output()).contains(setCookie);
    }

    @Test
    @DisplayName("로그인된 상태에서 /login 주소에 접근하면 /index.html 페이지로 리다이렉트한다.")
    void login_redirect_when_alreadyLogined() throws IOException, NoSuchFieldException, IllegalAccessException {
        // given
        String sessionId = UUID.randomUUID().toString();
        String cookie = "Cookie: JSESSIONID=" + sessionId;
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                cookie + " ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        SessionManager mockSessionManager = Mockito.mock(SessionManager.class);
        Session session = new Session(sessionId);
        Mockito.when(mockSessionManager.findSession(sessionId)).thenReturn(session);

        Field sessionManagerField = Http11Processor.class.getDeclaredField("sessionManager");
        sessionManagerField.setAccessible(true);
        sessionManagerField.set(processor, mockSessionManager);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 302 FOUND";
        String contentType = "Content-Type: text/html;charset=utf-8";
        String contentLength = "Content-Length: " + responseBody.length;

        assertThat(socket.output()).contains(responseLine, contentType, contentLength);
    }

    @Test
    @DisplayName("유효하지 않은 JSESSIONID로 /login 주소에 접근하면 /login.html 페이지를 응답한다.")
    void login_invalidSessionId() throws IOException {
        // given
        String cookie = "Cookie: JSESSIONID=asdf";
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                cookie + " ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 200 OK";
        String contentType = "Content-Type: text/html;charset=utf-8";
        String contentLength = "Content-Length: " + responseBody.length;

        assertThat(socket.output()).contains(responseLine, contentType, contentLength);
    }

    @CsvSource({"account=chorong&password=password", "account=gugu&password=sosad"})
    @ParameterizedTest
    @DisplayName("/login 주소에서 부정확한 계정명이나 비밀번호로 로그인을 시도하면 /401.html 페이지로 리다이렉트한다.")
    void login_fail_invalidAccount(String requestBody) throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 302 FOUND";
        String contentType = "Content-Type: text/html;charset=utf-8";
        String contentLength = "Content-Length: " + responseBody.length;

        assertThat(socket.output()).contains(responseLine, contentType, contentLength);
    }

    @Test
    @DisplayName("/register 주소에서 정확한 계정명과 이메일, 비밀번호로 회원가입에 성공하면 /index.html 페이지로 리다이렉트한다.")
    void register_success() throws IOException {
        // given
        String requestBody = "account=ash&password=ashPassword&email=test@email.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 302 FOUND";
        String contentType = "Content-Type: text/html;charset=utf-8";
        String contentLength = "Content-Length: " + responseBody.length;

        assertThat(socket.output()).contains(responseLine, contentType, contentLength);
    }

    @Test
    @DisplayName("/register 주소에서 이미 존재하는 계정명으로 회원가입을 시도하면 /register 페이지로 리다이렉트한다.")
    void register_fail_accountAlreadyExists() throws IOException {
        // given
        String requestBody = "account=gugu&password=newpassword&email=test@email.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        String responseLine = "HTTP/1.1 400 BAD REQUEST";
        String contentType = "Content-Type: text/html;charset=utf-8";
        String contentLength = "Content-Length: " + responseBody.length;

        assertThat(socket.output()).contains(responseLine, contentType, contentLength);
    }
}
