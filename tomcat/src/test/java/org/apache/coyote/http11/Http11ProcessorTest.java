package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK \r\n")
                .contains("Content-Length: 12")
                .contains("Set-Cookie: JSESSIONID=")
                .endsWith("\r\n\r\nHello world!");
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK \r\n")
                .contains("Content-Type: text/html;charset=utf-8")
                .contains("Set-Cookie: JSESSIONID=")
                .endsWith(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }

    @Test
    void css() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith(String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 "));
    }

    @Test
    void getLoginPage() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith(String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 "))
                .contains("<title>로그인</title>");
    }

    @Test
    void loginSuccessRedirectionToIndex() {
        // given
        final String body = "account=gugu&password=password";
        final String httpRequest = "POST /login HTTP/1.1\r\n"
                + "Content-Length: " + body.length() + "\r\n\r\n" + body;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith(String.join("\r\n",
                        "HTTP/1.1 302 Found",
                        "Location: /index.html"));
    }

    @Test
    void loginFailureRedirectionToUnauthorizedPage() {
        // given
        final String body = "account=gugu&password=wrong";
        final String httpRequest = "POST /login HTTP/1.1\r\n"
                + "Content-Length: " + body.length() + "\r\n\r\n" + body;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith(String.join("\r\n",
                        "HTTP/1.1 302 Found",
                        "Location: /401.html"));
    }

    @Test
    void getRegisterPage() {
        final var socket = new StubSocket("GET /register HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK").contains("<title>회원가입</title>");
    }

    @Test
    void registerWithPostBody() {
        final String body = "account=new-user&password=secret&email=new%40example.com";
        final String request = "POST /register HTTP/1.1\r\n"
                + "Content-Length: " + body.length() + "\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n\r\n" + body;
        final var socket = new StubSocket(request);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\nLocation: /index.html");
        assertThat(InMemoryUserRepository.findByAccount("new-user"))
                .hasValueSatisfying(user -> assertThat(user.checkPassword("secret")).isTrue());
    }

    @Test
    void existingSessionCookieIsNotSetAgain() {
        final String request = "GET /index.html HTTP/1.1\r\n"
                + "Cookie: other=value; JSESSIONID=existing-id\r\n\r\n";
        final var socket = new StubSocket(request);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK")
                .doesNotContain("Set-Cookie:");
    }

}
