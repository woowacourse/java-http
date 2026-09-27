package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import support.StubSocket;

class Http11ProcessorTest {

    private static final String CONTROLLER_PACKAGE = "com.techcourse.controller";
    private static final String SESSION_ID = "test-session-id";
    private static final String SESSION_COOKIE = "Cookie: JSESSIONID=" + SESSION_ID;
    private final RequestDispatcher requestDispatcher =
        new RequestDispatcher(new HandlerMapping(CONTROLLER_PACKAGE));

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
        final var processor = new Http11Processor(socket, requestDispatcher);

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

    @ParameterizedTest
    @CsvSource({
        "'GET /index', 'HTTP/1.1 400 Bad Request '",
        "'PUT /index HTTP/1.1', 'HTTP/1.1 405 Method Not Allowed '",
        "'GET /index HTTP/2.0', 'HTTP/1.1 505 HTTP Version Not Supported '",
        "'GET index HTTP/1.1', 'HTTP/1.1 400 Bad Request '"
    })
    void 잘못된_요청_줄에_해당하는_HTTP_오류를_응답한다(
        final String requestLine,
        final String expectedStatusLine
    ) {
        final String httpRequest = String.join("\r\n",
            requestLine,
            "Host: localhost:8080",
            "",
            "");
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor =
            new Http11Processor(socket, requestDispatcher);

        processor.process(socket);

        final String expected = String.join("\r\n",
            expectedStatusLine,
            "Content-Length: 0 ");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 빈_요청은_400을_응답한다() {
        final StubSocket socket = new StubSocket("");
        final Http11Processor processor =
            new Http11Processor(socket, requestDispatcher);

        processor.process(socket);

        final String expected = String.join("\r\n",
            "HTTP/1.1 400 Bad Request ",
            "Content-Length: 0 ");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 경로에서_지원하지_않는_메서드는_405를_응답한다() {
        final String httpRequest = String.join("\r\n",
            "POST /index HTTP/1.1",
            "Host: localhost:8080",
            "",
            "");
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor =
            new Http11Processor(socket, requestDispatcher);

        processor.process(socket);

        final String expected = String.join("\r\n",
            "HTTP/1.1 405 Method Not Allowed ",
            "Content-Length: 0 ");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            SESSION_COOKIE,
            "",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: 5564 \r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/css/styles.css", "/js/scripts.js"})
    void get(String targetFilePath) throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            String.format("GET %s HTTP/1.1", targetFilePath),
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            SESSION_COOKIE,
            "",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

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

    @ParameterizedTest
    @ValueSource(strings = {"/index", "/login", "/register"})
    void 세션이_필요하지_않은_GET_요청은_세션_쿠키를_발급하지_않는다(
        final String path
    ) {
        // given
        final String httpRequest = String.join("\r\n",
            "GET " + path + " HTTP/1.1",
            "Host: localhost:8080",
            "",
            "");
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).doesNotContain("Set-Cookie");
    }

    @Test
    void 존재하지_않는_정적_리소스를_요청하면_404를_응답한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /not-found.html HTTP/1.1",
            "Host: localhost:8080",
            "",
            "");
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        final String body = new String(Files.readAllBytes(new File(resource.getPath()).toPath()));
        final String expected = String.join("\r\n",
            "HTTP/1.1 404 Not Found ",
            "Content-Type: text/html;charset=utf-8 ",
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
            final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                SESSION_COOKIE,
                "",
                "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            final String body = new String(
                Files.readAllBytes(new File(resource.getPath()).toPath()));
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

            final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                SESSION_COOKIE,
                "",
                "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            final String body = new String(
                Files.readAllBytes(new File(resource.getPath()).toPath()));
            String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index ",
                "Content-Length: 0 ");

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void post_success() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
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
            final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output())
                .contains(
                    "HTTP/1.1 302 Found ",
                    "Location: /index ",
                    "Content-Length: 0 ")
                .containsPattern("Set-Cookie: JSESSIONID=[^;]+")
                .doesNotContain("Set-Cookie: JSESSIONID=" + SESSION_ID);
        }

        @Test
        void post_success_if_jsessionid_doesnt_exist() {
            // given
            final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept : */*",
                "",
                "account=gugu&password=password");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

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
            final String httpRequest = String.join("\r\n",
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
            final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

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
        void 로그인에_실패하면_세션_쿠키를_발급하지_않는다() {
            // given
            final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: 36",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=wrong-password");
            final StubSocket socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found ")
                .doesNotContain("Set-Cookie");
        }

        @Test
        void 존재하지_않는_세션_ID로_로그인에_실패해도_세션_쿠키를_발급하지_않는다() {
            // given
            final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: 36",
                "Content-Type: application/x-www-form-urlencoded",
                "Cookie: JSESSIONID=unknown-session-id",
                "",
                "account=gugu&password=wrong-password");
            final StubSocket socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found ")
                .doesNotContain("Set-Cookie");
        }


        @Test
        void post_failure1() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
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
            final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/401.html");
            final String body = new String(
                Files.readAllBytes(new File(resource.getPath()).toPath()));
            String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 2426 ",
                "",
                body);

            assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found ");
        }

        @Test
        void post_failure2() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
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
            final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/401.html");
            final String body = new String(
                Files.readAllBytes(new File(resource.getPath()).toPath()));
            String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 2426 ",
                "",
                body);

            assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found ");
        }
    }

    @Nested
    class Register {

        @Test
        void get() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                SESSION_COOKIE,
                "",
                "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

            // when
            processor.process(socket);

            // then
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            final String body = new String(
                Files.readAllBytes(new File(resource.getPath()).toPath()));
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
            final String httpRequest = String.join("\r\n",
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
            final Http11Processor processor = new Http11Processor(socket, requestDispatcher);

            // when
            processor.process(socket);

            // then
            String expected = String.join("\r\n",
                "HTTP/1.1 303 See Other ",
                "Location: /index ",
                "Content-Length: 0 ");

            assertThat(socket.output()).isEqualTo(expected);
        }

        @Test
        void 이미_존재하는_계정으로_회원가입하면_기존_회원정보를_유지한다() throws IOException {
            // given
            final String body =
                "account=gugu&password=changed&email=changed%40example.com";
            final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);
            final StubSocket socket = new StubSocket(httpRequest);
            final Http11Processor processor =
                new Http11Processor(socket, requestDispatcher);

            // when
            processor.process(socket);

            // then
            final User user = InMemoryUserRepository.findByAccount("gugu")
                .orElseThrow();
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            final String responseBody = new String(
                Files.readAllBytes(new File(resource.getPath()).toPath()));
            final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                String.format("Content-Length: %d ", responseBody.getBytes().length),
                "",
                responseBody);

            assertAll(
                () -> assertThat(user.checkPassword("password")).isTrue(),
                () -> assertThat(user.checkPassword("changed")).isFalse(),
                () -> assertThat(socket.output()).isEqualTo(expected)
            );
        }
    }
}
