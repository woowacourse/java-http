package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.http.header.HttpHeaderName;
import org.apache.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.StubSocket;

class Http11ProcessorTest {

    @Test
    @DisplayName("루트 Get 요청 처리")
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final HttpResponse httpResponse = HttpResponse.builder()
                .addHeader(HttpHeaderName.CONTENT_TYPE, "text/plain")
                .body("Hello world!")
                .okBuild();
        assertThat(socket.output()).isEqualTo(httpResponse.toString());
    }

    @Test
    @DisplayName("index.html Get 요청 처리")
    void index() throws IOException {
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
        final HttpResponse httpResponse = HttpResponse.builder()
                .addHeader(HttpHeaderName.CONTENT_TYPE, "text/html")
                .body(new String(Files.readAllBytes(new File(resource.getFile()).toPath())))
                .okBuild();
        assertThat(socket.output()).isEqualTo(httpResponse.toString());
    }

    @Test
    @DisplayName("login Get 요청 처리: 세션이 없는 경우 login html 로 리다이렉트")
    void login_Get_WhenExistsSession() {
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
        final HttpResponse httpResponse = HttpResponse.builder()
                .addHeader(HttpHeaderName.LOCATION, "/login.html")
                .foundBuild();
        assertThat(socket.output()).isEqualTo(httpResponse.toString());
    }

    @Test
    @DisplayName("login Get 요청 처리: 세션이 있는 경우 index html 리다이렉션")
    void login_Get_WhenNotExistsSession() {
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(new Session("1"));
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=1");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 302 Found", "Location: /index.html");
    }

    @Test
    @DisplayName("login Post 요청 처리: 성공 시 index html 리다이렉트 및 Set-Cookie 헤더에 세션값 추가")
    void login_Post() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: 30",
                "Connection: keep-alive",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 302 Found", "Location: /index.html", "Set-Cookie: JSESSIONID=");
    }

    @Test
    @DisplayName("register Get 요청 처리: 성공 시 register html 리다이렉트")
    void register_Get() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 302 Found", "Location: /register.html");
    }

    @Test
    @DisplayName("register Post 요청 처리: 성공 시 index html 리다이렉트")
    void register_Post() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: 49",
                "Connection: keep-alive",
                "",
                "account=gugu&email=hi@naver.com&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 302 Found", "Location: /index.html");
    }
}
