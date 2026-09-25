package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;


class Http11ProcessorTest {

    SessionManager manager = SessionManager.getInstance();
    Session session;

    @BeforeEach
    void setUp() {
        session = manager.createSession();
    }

    @AfterEach
    void tearDown() {
        manager.remove(session.getId());
    }

    @Test
    void 존재하지_않는_정적_리소스는_404로_응답한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /not-found.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        assertThatCode(() -> processor.process(socket))
                .doesNotThrowAnyException();

        // then
        String notFoundPage = Files.readString(new File(
                getClass().getClassLoader().getResource("static/404.html").getFile()
        ).toPath());
        assertThat(socket.output())
                .startsWith("HTTP/1.1 404 Not Found ")
                .contains(notFoundPage);
    }

    @Test
    void process() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                "Content-Length: 12 \r\n",
                "\r\n" + "Hello world!"
        );
    }

    @Nested
    @DisplayName("header section의 끝에는 CRLF가 존재한다")
    class end_of_the_header_section {

        @Test
        void message_body가_존재하는_경우() {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET / HTTP/1.1",
                    "Host: localhost:8080",
                    "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                    "",
                    "");
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String output = socket.output();
            assertThat(output).startsWith("HTTP/1.1 200 OK \r\n");
            assertThat(output).contains(
                    "Content-Type: text/html;charset=utf-8 \r\n",
                    "Content-Length: 12 \r\n"
            );
            assertThat(output).endsWith(" \r\n\r\n" + "Hello world!");
        }

        @Test
        void message_body가_존재하지_않는_경우() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                    "Connection: keep-alive ",
                    "Content-Length: 30",
                    "Content-Type: application/x-www-form-urlencoded",
                    "Accept: */*",
                    "\r\n" +
                            "account=gugu&password=password",
                    "",
                    "");
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String output = socket.output();
            assertThat(output).startsWith("HTTP/1.1 302 Found \r\n");
            assertThat(output).contains(
                    "Location: http://localhost:8080/index.html \r\n",
                    "Content-Type: text/html;charset=utf-8 \r\n",
                    "Content-Length: 0 \r\n"
            );
            assertThat(output).endsWith(" \r\n\r\n");
        }
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
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
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                "Content-Length: 5564 \r\n",
                "\r\n" + new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    @Test
    void login_get() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
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
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                "Content-Length: 3797 \r\n",
                "\r\n" + new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    @Test
    @DisplayName("로그인된 상태에서 /login 페이지에 HTTP GET METHOD로 접근하면 index.html 페이지로 리다이렉트 처리한다")
    void login_get_with_logged_in_session() throws IOException {
        // given
        SessionManager manager = SessionManager.getInstance();
        Session session = manager.createSession();
        String sessionId = session.getId();
        session.setAttribute("user", new User("sample", "sample", "sample@example.com"));
        manager.add(session);
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Cookie: JSESSIONID=" + sessionId,
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found \r\n",
                "Location: http://localhost:8080/index.html \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                "Content-Length: 0 \r\n"
        );
        manager.remove(sessionId);
    }

    @Test
    void login_post_success() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "\r\n" +
                        "account=gugu&password=password",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found \r\n",
                "Location: http://localhost:8080/index.html \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                "Content-Length: 0 \r\n"
        );
    }

    @Test
    void 요청에_쿠키가_없으면_JSESSIONID를_발급한다() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String cookieHeader = socket.output().lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Set-Cookie: JSESSIONID 헤더가 존재하지 않습니다."));
        String sessionId = cookieHeader
                .substring("Set-Cookie: JSESSIONID=".length())
                .strip();

        assertThat(UUID.fromString(sessionId).toString()).isEqualTo(sessionId);
    }

    @Test
    void 요청에_이미_JSession_쿠키_헤더가_있다면_응답에_포함하지_않는다() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=" + session.getId(),
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "\r\n" +
                        "account=gugu&password=password",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).doesNotContain("Set-Cookie:");
    }

    @Test
    void login_post_fail() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "Connection: keep-alive ",
                "Content-Length: 41",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "\r\n" +
                        "account=failAccount&password=failPassword",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found \r\n",
                "Location: http://localhost:8080/401.html \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                "Content-Length: 0 \r\n"
        );
    }

    @Test
    void register_get() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                "Content-Length: 4319 \r\n",
                "\r\n" + new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
        );
    }

    @Test
    void register_post_success() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "Connection: keep-alive ",
                "Content-Length: 56",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "\r\n" +
                        "account=rude&email=rudevi@woowahan.com&password=password",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found \r\n",
                "Location: http://localhost:8080/index.html \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                "Content-Length: 0 \r\n"
        );
    }

    @Test
    void register_post_fail() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "Connection: keep-alive ",
                "Content-Length: 56",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "\r\n" +
                        "account=gugu&email=hkkang@woowahan.com&password=password",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found \r\n",
                "Location: http://localhost:8080/login.html \r\n",
                "Content-Type: text/html;charset=utf-8 \r\n",
                "Content-Length: 0 \r\n"
        );
    }
}
