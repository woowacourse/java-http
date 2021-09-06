package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class RequestHandlerTest {

    @Test
    void run() {
        // given
        final MockSocket socket = new MockSocket();
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        String expected = String.join("\r\n",
            "HTTP/1.1 200 OK",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: 12",
            "",
            "Hello world!");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("index로 접속하면, index.html을 보여준다.")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /index.html HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Cookie: hey=man",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "Content-Length: 5564\r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }


    @DisplayName("login으로 접속하면 login.html을 보여준다.")
    @Test
    void loginPageTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /login HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Cookie: hey=man",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "Content-Length: 3797\r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인에 성공하면, 302 Found를 받는다.")
    @Test
    void loginSuccessTest() {
        try (MockedStatic<UUID> uuidMockedStatic = mockStatic(UUID.class)) {
            UUID uuid = new UUID(1L, 2L);
            given(UUID.randomUUID()).willReturn(uuid);
            String expectedJSESSIONID = "JSESSIONID=00000000-0000-0001-0000-000000000002";
            // given
            final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Cookie: hey=man",
                "",
                "account=gugu&password=password"
            );

            final MockSocket socket = new MockSocket(httpRequest);
            final RequestHandler requestHandler = new RequestHandler(socket);

            // when
            requestHandler.run();

            // then
            String expected = "HTTP/1.1 302 Found\r\n" +
                "Set-Cookie: " + expectedJSESSIONID + "\r\n" +
                "Location: /index.html\r\n" +
                "\r\n";
            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @DisplayName("로그인 시도시 잘못된 아이디를 입력하면, 401.html을 보여준다.")
    @Test
    void loginFailTest1() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /login HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Content-Length: 32",
            "Cookie: hey=man",
            "",
            "account=mungto&password=password");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expected = "HTTP/1.1 401 Unauthorized\r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 시도시 잘못된 비밀번호를 입력하면, 401.html을 보여준다.")
    @Test
    void loginFailTest2() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /login HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Content-Length: 35",
            "Cookie: hey=man",
            "",
            "account=gugu&password=wrongpassword");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expected = "HTTP/1.1 401 Unauthorized\r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("register로 접속하면, 200코드를 받고 register.html을 보여준다.")
    @Test
    void registerPageTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /register HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "Cookie: hey=man",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expected = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "Content-Length: 4319\r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원 가입에 성공하면, 200코드를 받고 index.html를 보여준다.")
    @Test
    void registerPostSuccessTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /register HTTP/1.1",
            "Host: localhost:8080",
            "Content-Length: 58",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: */*",
            " ",
            "account=fortune&password=password&email=hahaha%40woowahan.com"
        );

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK\r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원가입에 실패하면, 409코드를 받고 register.html을 보여준다.")
    @Test
    void registerFailPageTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /register HTTP/1.1",
            "Host: localhost:8080",
            "Content-Length: 58",
            "Content-Type: application/x-www-form-urlencoded",
            "Accept: */*",
            " ",
            "account=gugu&password=password&email=hahaha%40woowahan.com"
        );

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expected = "HTTP/1.1 409 Conflict\r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("잘못된 경로로 접근하면, 404코드를 받고 404.html을 보여준다.")
    @Test
    void notFoundPageTest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /wrongwrongwrongwrong.html HTTP/1.1",
            "Host: localhost:8080",
            "Connection: keep-alive",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        // when
        requestHandler.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        String expected = "HTTP/1.1 404 Not Found\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Nested
    @DisplayName("쿠키에 JSESSIONID가 있을때, ")
    class JsessionidContain {

        @DisplayName("register로 접속하면, 200코드를 받고 register.html을 보여준다.")
        @Test
        void registerPageTest() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final RequestHandler requestHandler = new RequestHandler(socket);

            // when
            requestHandler.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            String expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 4319\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("로그인을 한뒤 login페이지에 재접근시, index.html을 보여준다.")
        @Test
        void sessionLoginTest() {
            try (MockedStatic<UUID> uuidMockedStatic = mockStatic(UUID.class)) {
                UUID uuid = new UUID(1L, 2L);
                given(UUID.randomUUID()).willReturn(uuid);
                String expectedJSESSIONID = "JSESSIONID=00000000-0000-0001-0000-000000000002";
                // given
                String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Content-Length: 30",
                    "Cookie: " + expectedJSESSIONID,
                    "",
                    "account=gugu&password=password"
                );

                MockSocket socket = new MockSocket(httpRequest);
                RequestHandler requestHandler = new RequestHandler(socket);
                requestHandler.run();
                socket.output();

                // when
                httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Cookie: " + expectedJSESSIONID,
                    "",
                    "");

                socket = new MockSocket(httpRequest);
                requestHandler = new RequestHandler(socket);
                requestHandler.run();

                // then
                String expected = "HTTP/1.1 302 Found\r\n" +
                    "Location: /index.html\r\n" +
                    "\r\n";
                assertThat(socket.output()).isEqualTo(expected);
            }
        }
    }
}
