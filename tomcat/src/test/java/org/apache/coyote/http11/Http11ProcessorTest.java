package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("1단계 - HTTP 서버 구현")
    @Nested
    class Step1 {

        @DisplayName("/로 인덱스 페이지에 접근할 수 있다.")
        @Test
        void process() {
            // given
            final var socket = new StubSocket();
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).contains(
                    "HTTP/1.1 200 OK",
                    "Content-Type: text/html;charset=utf-8",
                    "Content-Length: 12",
                    "Hello world!"
            );
        }

        @DisplayName("/index.html로 인덱스 페이지에 접근할 수 있다.")
        @Test
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
            URL resource = getClass().getClassLoader().getResource("static/index.html");
            String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).contains(
                    "HTTP/1.1 200 OK",
                    "Content-Type: text/html;charset=utf-8",
                    "Content-Length: 5564",
                    expectedBody
            );
        }

        @DisplayName("CSS 파일에 접근할 수 있다.")
        @Test
        void css() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /css/styles.css HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Accept: text/css,*/*;q=0.1 ",
                    "Connection: keep-alive",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
            String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).contains(
                    "HTTP/1.1 200 OK",
                    "Content-Type: text/css;charset=utf-8",
                    "Content-Length: " + expectedBody.getBytes().length,
                    expectedBody
            );
        }

        @DisplayName("/login으로 로그인 페이지에 접근할 수 있다.")
        @Test
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
            URL resource = getClass().getClassLoader().getResource("static/login.html");
            String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output()).contains(
                    "HTTP/1.1 200 OK",
                    "Content-Type: text/html;charset=utf-8",
                    "Content-Length: " + expectedBody.getBytes().length,
                    expectedBody
            );
        }
    }

    @DisplayName("2단계 - 로그인 구현")
    @Nested
    class Step2 {

        @DisplayName("로그인 성공 시, 302을 반환하고 `/index.html`로 리다이렉트 한다.")
        @Test
        void loginSuccess() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                    "Content-Length: 30 ",
                    "Connection: keep-alive ",
                    "",
                    "account=gugu&password=password ");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).contains(
                    "HTTP/1.1 302 Found",
                    "Location: http://localhost:8080/index.html",
                    "Content-Type: text/html;charset=utf-8",
                    "Content-Length: 0"
            );
        }

        @DisplayName("로그인 실패 시, 302을 반환하고 `/401.html`로 리다이렉트 한다.")
        @Test
        void loginFailure() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: 35 ",
                    "Connection: keep-alive ",
                    "",
                    "account=gugu&password=wrongPassword ");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).contains(
                    "HTTP/1.1 302 Found",
                    "Location: http://localhost:8080/401.html",
                    "Content-Type: text/html;charset=utf-8",
                    "Content-Length: 0"
            );
        }

        @DisplayName("/register로 회원가입 페이지에 접근할 수 있다.")
        @Test
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
            URL resource = getClass().getClassLoader().getResource("static/register.html");
            String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
            assertThat(socket.output()).contains(
                    "HTTP/1.1 200 OK",
                    "Content-Type: text/html;charset=utf-8",
                    "Content-Length: " + expectedBody.getBytes().length,
                    expectedBody
            );
        }

        @DisplayName("회원가입 성공 시, index 페이지로 리다이렉트 한다.")
        @Test
        void registerSuccess() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 80 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "Accept: */* ",
                    "",
                    "account=gugu&password=password&email=hkkang%40woowahan.com");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).contains(
                    "HTTP/1.1 302 Found",
                    "Location: http://localhost:8080/index.html",
                    "Content-Type: text/html;charset=utf-8",
                    "Content-Length: 0"
            );
        }

        @DisplayName("회원가입 폼 입력이 올바르지 않는 경우 예외가 발생한다.")
        @ParameterizedTest
        @ValueSource(strings = {"account=gugu", "wrongaccount=gugu&password=password&email=hkkang%40woowahan.com"})
        void registerFailure(String invalidBody) {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 80 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "Accept: */* ",
                    "",
                    invalidBody);

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when & then
            assertThatThrownBy(() -> processor.process(socket))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @DisplayName("로그인 시, Cookie에 JSESSIONID가 없으면 응답 헤더에 Set-Cookie를 반환한다.")
        @Test
        void setCookieWithNoCookieLogin() {
            // given
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: 30 ",
                    "Connection: keep-alive ",
                    "",
                    "account=gugu&password=password ");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).contains("Set-Cookie: JSESSIONID");
        }

        @DisplayName("로그인 시 Session 객체의 값으로 User 객체를 저장하고, 로그인 페이지 접속 시 index로 리다이렉트 한다.")
        @Test
        void addSession() {
            // given
            final String loginRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: 30 ",
                    "Connection: keep-alive ",
                    "",
                    "account=gugu&password=password ");

            final var socket = new StubSocket(loginRequest);
            final Http11Processor processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            String response = socket.output();
            int index = response.lastIndexOf("JSESSIONID=");
            String sessionId = response.substring(index + 11, index + 47);

            assertThatCode(() -> SessionManager.getInstance().findSession(sessionId))
                    .doesNotThrowAnyException();

            // given
            final String loginPageRequest = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Cookie: JSESSIONID=" + sessionId,
                    "Connection: keep-alive ",
                    "");

            final var socket2 = new StubSocket(loginPageRequest);
            final Http11Processor processor2 = new Http11Processor(socket2);

            // when
            processor2.process(socket2);

            // then
            assertThat(socket.output()).contains(
                    "HTTP/1.1 302 Found",
                    "Location: http://localhost:8080/index.html",
                    "Content-Type: text/html;charset=utf-8",
                    "Content-Length: 0"
            );
        }
    }
}
