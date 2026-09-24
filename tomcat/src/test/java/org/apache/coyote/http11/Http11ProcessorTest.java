package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class Http11ProcessorTest {

    private static final String SESSION_ID = "test-session-id";
    private static final String SESSION_COOKIE = "Cookie: JSESSIONID=" + SESSION_ID;

    @BeforeEach
    void setUp() {
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.remove(SESSION_ID);
        sessionManager.add(Session.init(SESSION_ID));
    }

    @Test
    void process() {
        // given
        final String httpRequest = String.join("\r\n",
            "GET / HTTP/1.1",
            "Host: localhost:8080",
            SESSION_COOKIE,
            "",
            "");
        final var socket = new StubSocket(httpRequest);
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

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                SESSION_COOKIE,
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/css/styles.css", "/js/scripts.js"})
    void get(String targetFilePath) throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
            String.format("GET %s HTTP/1.1", targetFilePath),
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            SESSION_COOKIE,
            "",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static" + targetFilePath);
        final String body = new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
        String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            String.format("Content-Type: text/%s;charset=utf-8 ", parseExtension(targetFilePath)),
            String.format("Content-Length: %d ", body.getBytes().length),
            "",
            body);

        assertThat(socket.output()).isEqualTo(expected);
    }

    private String parseExtension(String filePath) {
        final int startIndex = filePath.indexOf(".");
        return filePath.substring(startIndex + 1);
    }

    @Nested
    class Login {

        @Test
        void get_not_login() throws IOException {
            // given
            final String httpRequest= String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                SESSION_COOKIE,
                "",
                "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            final String body = new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
            String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                String.format("Content-Length: %d ", body.getBytes().length),
                "",
                body);

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void get_already_login() throws IOException {
            // given
            final User user = InMemoryUserRepository.findByAccount("gugu")
                .orElseThrow();
            final Session session = SessionManager.getInstance()
                .findSession(SESSION_ID);
            session.addAttribute("user", user);

            final String httpRequest= String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                SESSION_COOKIE,
                "",
                "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            final String body = new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
            String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index ",
                "Content-Length: 0 ");

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void post_success() throws IOException {
            // given
            final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept : */*",
                SESSION_COOKIE,
                "",
                "account=gugu&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index ",
                "Content-Length: 0 ");

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void post_success_if_jsessionid_doesnt_exist() {
            // given
            final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept : */*",
                "",
                "account=gugu&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final String actual = socket.output();

            assertAll(
                () -> assertThat(actual).contains(
                    "HTTP/1.1 302 Found ",
                    "Location: /index ",
                    "Content-Length: 0 "),
                () -> assertThat(actual).containsPattern(
                    "Set-Cookie: JSESSIONID=[^;]+")
            );
        }

        @Test
        void post_login_with_unknown_session_id_issues_new_session() {
            // given
            final String wrongSessionCookie = "Cookie: JSESSIONID=wrong-jsessionid";
            final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept : */*",
                wrongSessionCookie,
                "",
                "account=gugu&password=password");

            final StubSocket socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final String actual = socket.output();

            assertAll(
                () -> assertThat(actual).contains(
                    "HTTP/1.1 302 Found ",
                    "Location: /index "),
                () -> assertThat(actual).containsPattern(
                    "Set-Cookie: JSESSIONID=[^;]+")
                    .doesNotContain("Set-Cookie: JSESSIONID=wrong-jsessionid")
            );
        }


        @Test
        void post_failure1() throws IOException {
            // given
            final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept : */*",
                SESSION_COOKIE,
                "",
                "account=gugu&password=passwor");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/401.html");
            final String body = new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
            String expected = String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 2426 ",
                "",
                body);

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void post_failure2() throws IOException {
            // given
            final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept : */*",
                SESSION_COOKIE,
                "",
                "account=emptyuser&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/401.html");
            final String body = new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
            String expected = String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 2426 ",
                "",
                body);

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @Nested
    class Register {

        @Test
        void get() throws IOException {
            // given
            final String httpRequest= String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                SESSION_COOKIE,
                "",
                "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            final String body = new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
            String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                String.format("Content-Length: %d ", body.getBytes().length),
                "",
                body);

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void post() throws IOException {
            // given
            final String httpRequest= String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 58 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept : */*",
                SESSION_COOKIE,
                "",
                "account=kios&password=password&email=kios@woowahan.com");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String expected = String.join("\r\n",
                "HTTP/1.1 303 See Other ",
                "Location: /index ",
                "Content-Length: 0 ");

            assertThat(socket.output()).isEqualTo(expected);
        }
    }
}
