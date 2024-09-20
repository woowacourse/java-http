package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.message.parser.HttpRequestParser;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("GET /login")
    @Nested
    class doGet {

        @DisplayName("로그인 되어 있으면 index 페이지로 이동한다.")
        @Test
        void getIndexPageWithLogin() throws IOException {
            String sessionId = loginJojo();

            String request = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Cookie: JSESSIONID=" + sessionId,
                    "Connection: keep-alive ",
                    "");

            HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));
            HttpResponse httpResponse = new HttpResponse();

            // when
            new LoginController().doGet(httpRequest, httpResponse);

            // then
            assertThat(httpResponse.convertMessage()).contains(
                    "HTTP/1.1 302 Found",
                    "Location: http://localhost:8080/index.html",
                    "Content-Type: text/html;charset=utf-8",
                    "Content-Length: 0"
            );
        }

        private String loginJojo() {
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute("user", new User("jojo", "1234", "jojo@email.com"));
            SessionManager.getInstance().add(session);
            return session.getId();
        }

        @DisplayName("로그인 안 되어있으면 login 페이지 경로를 viewUri에 담는다.")
        @Test
        void getLoginPageWithNoLogin() throws IOException {
            String request = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));
            HttpResponse httpResponse = new HttpResponse();

            // when
            new LoginController().doGet(httpRequest, httpResponse);

            // then
            assertThat(httpResponse.findViewUri()).isPresent()
                    .hasValue("/login.html");
        }
    }

    @DisplayName("POST /login")
    @Nested
    class doPost {

        @DisplayName("로그인 성공하면 index 페이지로 이동한다.")
        @Test
        void loginSuccess() throws IOException {
            String request = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; ",
                    "Content-Length: 30 ",
                    "Connection: keep-alive ",
                    "",
                    "account=gugu&password=password ");

            HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));
            HttpResponse httpResponse = new HttpResponse();

            // when
            new LoginController().doPost(httpRequest, httpResponse);

            // then
            assertThat(httpResponse.convertMessage()).contains(
                    "HTTP/1.1 302 Found",
                    "Location: http://localhost:8080/index.html",
                    "Content-Type: text/html;charset=utf-8",
                    "Set-Cookie: JSESSIONID=",
                    "Content-Length: 0"
            );
        }

        @DisplayName("로그인 실패하면 404 페이지로 이동한다.")
        @Test
        void loginFailure() throws IOException {
            String request = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; ",
                    "Content-Length: 30 ",
                    "Connection: keep-alive ",
                    "",
                    "account=gugu&password=wrongPassword ");

            HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));
            HttpResponse httpResponse = new HttpResponse();

            // when
            new LoginController().doPost(httpRequest, httpResponse);

            // then
            assertThat(httpResponse.convertMessage()).contains(
                    "HTTP/1.1 302 Found",
                    "Location: http://localhost:8080/401.html",
                    "Content-Type: text/html;charset=utf-8",
                    "Content-Length: 0"
            );
        }
    }
}
