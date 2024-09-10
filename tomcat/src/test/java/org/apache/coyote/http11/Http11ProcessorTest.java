package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.techcourse.model.User;

import support.StubSocket;

class Http11ProcessorTest {
    private static final String FAKE_SESSION_ID = "abcdefghijklmnopqrstuvwxyz";

    @DisplayName("index 페이지를 조회할 수 있다.")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String resourceName = "static/index.html";
        final URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
        assert resourceUrl != null;
        final String contentLength = String.valueOf(new File(resourceUrl.getFile()).length());
        final String responseBody = new String(Files.readAllBytes(new File(resourceUrl.getFile()).toPath()));
        final String httpResponse = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: " + contentLength,
                "Content-Type: text/html;charset=utf-8",
                "",
                responseBody);

        assertThat(socket.output()).isEqualTo(httpResponse);
    }

    @DisplayName("css 파일을 조회할 수 있다.")
    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String resourceName = "static/css/styles.css";
        final URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
        assert resourceUrl != null;
        final String contentLength = String.valueOf(new File(resourceUrl.getFile()).length());
        final String responseBody = new String(Files.readAllBytes(new File(resourceUrl.getFile()).toPath()));
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: " + contentLength,
                "Content-Type: text/css;charset=utf-8",
                "",
                responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("login 페이지를 조회할 수 있다.")
    @Test
    void login() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String resourceName = "static/login.html";
        final URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
        assert resourceUrl != null;
        final String contentLength = String.valueOf(new File(resourceUrl.getFile()).length());
        final String responseBody = new String(Files.readAllBytes(new File(resourceUrl.getFile()).toPath()));
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: " + contentLength,
                "Content-Type: text/html;charset=utf-8",
                "",
                responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인에 성공하면, index.html 페이지로 리다이렉트한다.")
    @Test
    void redirectIndexPageWhenLoginSuccess() {
        // given
        final String account = "gugu";
        final String correctPassword = "password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=" + account + "&" + "password=" + correctPassword);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String output = socket.output();

        assertAll(
                () -> assertThat(output).contains("HTTP/1.1 302 Found"),
                () -> assertThat(output).contains("Location: /index.html")
        );
    }

    @DisplayName("로그인에 성공할 때 Request Header의 Cookie에 JSESSIONID가 없으면, Response Header에 Set-Cookie를 반환한다.")
    @Test
    void createJSessionIdIfNotExistWhenLoginSuccess() {
        // given
        final String account = "gugu";
        final String correctPassword = "password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=" + account + "&" + "password=" + correctPassword);
        String sessionIdPattern = "Set-Cookie: JSESSIONID=[a-fA-F0-9\\-]{36}";
        Pattern pattern = Pattern.compile(sessionIdPattern);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String output = socket.output();

        assertAll(
                () -> assertThat(output).contains("HTTP/1.1 302 Found"),
                () -> assertThat(output).contains("Location: /index.html"),
                () -> assertThat(pattern.matcher(output).find()).isTrue()
        );
    }

    @DisplayName("로그인에 실패하면, 401.html 페이지로 리다이렉트한다.")
    @Test
    void redirect401PageWhenLoginFail() {
        // given
        final String account = "gugu";
        final String wrongPassword = "gugu";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=" + account + "&" + "password=" + wrongPassword);
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /401.html",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("register 페이지를 조회할 수 있다.")
    @Test
    void register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String resourceName = "static/register.html";
        final URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
        assert resourceUrl != null;
        final String contentLength = String.valueOf(new File(resourceUrl.getFile()).length());
        final String responseBody = new String(Files.readAllBytes(new File(resourceUrl.getFile()).toPath()));
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: " + contentLength,
                "Content-Type: text/html;charset=utf-8",
                "",
                responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원가입이 완료되면, index.html 페이지로 리다이렉트한다.")
    @Test
    void redirectIndexPageWhenRegisterComplete() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 59",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /index.html",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }


    private long getFileSize(URL resource) {
        return new File(resource.getFile()).length();
    }
}
