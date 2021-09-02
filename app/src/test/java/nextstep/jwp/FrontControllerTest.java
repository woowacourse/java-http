package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.exception.controller.DuplicateRegisterException;
import nextstep.jwp.exception.controller.UnAuthorizationException;
import nextstep.jwp.exception.http.response.ContentTypeNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FrontControllerTest {

    @DisplayName("애플리케이션을 실행한다.")
    @Test
    void run() throws IOException {
        // given
        final MockSocket socket = new MockSocket();
        final FrontController frontController = new FrontController(socket);

        // when
        frontController.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Length: 5564 \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("home 페이지를 조회한다.")
    @Test
    void home() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
            "GET / HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
            "",
            "");

        final MockSocket socket = new MockSocket(httpRequest);
        final FrontController frontController = new FrontController(socket);

        // when
        frontController.run();

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Length: 5564 \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("index 페이지를 조회한다.")
    @Nested
    class GetIndex {

        @DisplayName("확장자가 없는 경우")
        @Test
        void withoutExtension() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /index HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 5564 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("확장자가 있는 경우")
        @Test
        void withExtension() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 5564 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @DisplayName("login 페이지를 조회한다.")
    @Nested
    class GetLogin {

        @DisplayName("확장자가 없는 경우")
        @Test
        void withoutExtension() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 3797 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("확장자가 있는 경우")
        @Test
        void withExtension() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 3797 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @DisplayName("로그인을 한다.")
    @Nested
    class PostLogin {

        @DisplayName("로그인 성공")
        @Test
        void success() throws IOException {
            // given
            String body = "account=gugu&password=password";
            final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                body);

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Content-Length: 5564 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("로그인 실패")
        @Test
        void fail() throws IOException {
            // given
            String body = "account=dada&password=dada";
            final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                body);

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            // then
            assertThatCode(frontController::run)
                .isInstanceOf(UnAuthorizationException.class)
                .hasMessage("접근 권한이 없습니다.")
                .hasFieldOrPropertyWithValue("httpStatus", "401");
        }
    }

    @DisplayName("register 페이지를 조회한다.")
    @Nested
    class GetRegister {

        @DisplayName("확장자가 없는 경우")
        @Test
        void withoutExtension() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            final byte[] fileBytes = Files.readAllBytes(new File(resource.getFile()).toPath());

            String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: " + fileBytes.length + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(fileBytes);

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("확장자가 있는 경우")
        @Test
        void withExtension() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /register.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            final byte[] fileBytes = Files.readAllBytes(new File(resource.getFile()).toPath());

            String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: " + fileBytes.length + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(fileBytes);

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @DisplayName("회원가입을 한다.")
    @Nested
    class PostRegister {

        @DisplayName("회원가입 성공")
        @Test
        void success() throws IOException {
            // given
            String body = "account=dani&password=dani&email=dani@woowahan.com";
            final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                body);

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            final byte[] fileBytes = Files.readAllBytes(new File(resource.getFile()).toPath());

            String expected = "HTTP/1.1 201 CREATED \r\n" +
                "Content-Length: " + fileBytes.length + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(fileBytes);

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("회원가입 실패")
        @Test
        void fail() {
            // given
            String body = "account=gugu&password=password&email=hkkang@woowahan.com";
            final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                body);

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            assertThatThrownBy(frontController::run)
                .isInstanceOf(DuplicateRegisterException.class)
                .hasMessage("이미 회원가입되어 있습니다.")
                .hasFieldOrPropertyWithValue("httpStatus", "400");
        }
    }

    @DisplayName("쿠키를 사용한다.")
    @Nested
    class Cookie {

        @DisplayName("HTTP Request Header - Cookie O")
        @Test
        void isCookie() throws IOException {
            // given
            String body = "account=gugu&password=password";
            final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                body);

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            final byte[] fileBytes = Files.readAllBytes(new File(resource.getFile()).toPath());

            final String expected = "HTTP/1.1 302 FOUND \r\n" +
                "Content-Length: " + fileBytes.length + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(fileBytes);

            assertThat(socket.output()).isEqualTo(expected);

        }

        @DisplayName("HTTP Request Header - Cookie X")
        @Test
        void isNoCookie() {
            // given
            String body = "account=gugu&password=password";
            final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                body);

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            assertThat(socket.output()).contains("Set-Cookie");
        }
    }

    @DisplayName("404 예외 페이지를 조회한다. - 유효하지 않은 URI")
    @Nested
    class notFound {

        @DisplayName("GET 요청")
        @Test
        void get() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /dani HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/404.html");
            final byte[] fileBytes = Files.readAllBytes(new File(resource.getFile()).toPath());

            String expected = "HTTP/1.1 404 NOT FOUND \r\n" +
                "Content-Length: " + fileBytes.length + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(fileBytes);

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("POST 요청")
        @Test
        void post() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "POST /dani HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/404.html");
            final byte[] fileBytes = Files.readAllBytes(new File(resource.getFile()).toPath());

            String expected = "HTTP/1.1 404 NOT FOUND \r\n" +
                "Content-Length: " + fileBytes.length + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(fileBytes);

            assertThat(socket.output()).isEqualTo(expected);
        }
    }

    @DisplayName("정적 파일을 조회한다.")
    @Nested
    class Resource {

        @DisplayName("GET 요청")
        @Test
        void get() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /500.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/500.html");
            final byte[] fileBytes = Files.readAllBytes(new File(resource.getFile()).toPath());

            String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: " + fileBytes.length + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(fileBytes);

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("POST 요청")
        @Test
        void post() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                "POST /500.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            frontController.run();

            // then
            final URL resource = getClass().getClassLoader().getResource("static/400.html");
            final byte[] fileBytes = Files.readAllBytes(new File(resource.getFile()).toPath());

            String expected = "HTTP/1.1 400 BAD REQUEST \r\n" +
                "Content-Length: " + fileBytes.length + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(fileBytes);

            assertThat(socket.output()).isEqualTo(expected);
        }

        @DisplayName("유효하지 않은 Content Type")
        @Test
        void invalidContentType() {
            // given
            final String httpRequest = String.join("\r\n",
                "GET /dani.png HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

            final MockSocket socket = new MockSocket(httpRequest);
            final FrontController frontController = new FrontController(socket);

            // when
            // then
            assertThatCode(frontController::run)
                .isInstanceOf(ContentTypeNotFoundException.class)
                .hasMessage("존재하지 않는 Content-Type입니다.")
                .hasFieldOrPropertyWithValue("httpStatus", "400");
        }
    }
}
