package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import org.apache.catalina.auth.Session;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.request.RequestBody;
import org.apache.catalina.request.RequestHeader;
import org.apache.catalina.request.RequestLine;
import org.apache.catalina.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import com.techcourse.service.AuthService;

class LoginControllerTest {

    @Nested
    @DisplayName("관련된 컨트롤러 여부")
    class isMatchesRequest {
        @ParameterizedTest
        @ValueSource(strings = {"/login", "/login?account=gugu&password=password"})
        @DisplayName("성공 : 연관된 path일 경우 true 반환")
        void isMatchesRequestReturnTrue(String path) {
            HttpRequest httpRequest = new HttpRequest(
                    new RequestLine("GET " + path + " HTTP/1.1"),
                    new RequestHeader(),
                    new RequestBody()
            );

            boolean actual = new LoginController().isMatchesRequest(httpRequest);

            assertThat(actual).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "login",
                "/login2",
                "login?account=gugu&password=password",
                "/login2?account=gugu&password=password",
        })
        @DisplayName("성공 : 연관되지 않은 path일 경우 false 반환")
        void isMatchesRequestReturnFalse(String path) {
            HttpRequest httpRequest = new HttpRequest(
                    new RequestLine("GET " + path + " HTTP/1.1"),
                    new RequestHeader(),
                    new RequestBody()
            );

            boolean actual = new LoginController().isMatchesRequest(httpRequest);

            assertThat(actual).isFalse();
        }
    }

    @Nested
    @DisplayName("Get 메서드 요청")
    class doGet {
        @Test
        @DisplayName("성공 : 로그인 성공 후 접근 시 index.html response 반환")
        void doGetSuccessLoginSuccess() throws IOException {
            User user = new User("kyum", "password", "kyum@naver.com");
            InMemoryUserRepository.save(user);
            Session session = new AuthService().createSession(user);
            HttpRequest request = new HttpRequest(
                    new RequestLine("GET /login HTTP/1.1"),
                    new RequestHeader(Map.of("Cookie", "JSESSIONID=" + session.getId())),
                    new RequestBody()
            );
            HttpResponse response = HttpResponse.of(request);

            new LoginController().doGet(request, response);

            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());
            var expected = "HTTP/1.1 302 Found \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: " + bytes.length + " \r\n" +
                    "Location: http://localhost:8080/index.html" + " \r\n" +
                    "\r\n" +
                    new String(bytes);

            String actual = response.toString();
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("성공 : 파라미터가 없이 접근 시 login.html response 반환")
        void doGetSuccessNotParameter() throws IOException {
            User user = new User("kyum", "password", "kyum@naver.com");
            InMemoryUserRepository.save(user);
            HttpRequest request = new HttpRequest(
                    new RequestLine("GET /login HTTP/1.1"),
                    new RequestHeader(),
                    new RequestBody()
            );
            HttpResponse response = HttpResponse.of(request);

            new LoginController().doGet(request, response);

            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());
            var expected = "HTTP/1.1 200 OK \r\n" +
                    "Content-Type: text/html;charset=utf-8 \r\n" +
                    "Content-Length: " + bytes.length + " \r\n" +
                    "\r\n" +
                    new String(bytes);

            String actual = response.toString();
            assertThat(actual).isEqualTo(expected);
        }

        @Nested
        @DisplayName("(파라미터 존재할 때)로그인 시도")
        class doGetSuccessContainParameter {
            @Test
            @DisplayName("성공 : login 시도 성공 시 index.html response 반환")
            void doGetSuccessContainParameterSuccess() throws IOException {
                User user = new User("kyum", "password", "kyum@naver.com");
                InMemoryUserRepository.save(user);
                HttpRequest request = new HttpRequest(
                        new RequestLine("GET /login?account=kyum&password=password HTTP/1.1"),
                        new RequestHeader(),
                        new RequestBody()
                );
                HttpResponse response = HttpResponse.of(request);

                new LoginController().doGet(request, response);

                final URL resource = getClass().getClassLoader().getResource("static/index.html");
                byte[] bytes = Files.readAllBytes(new File(resource.getFile()).toPath());
                String actual = response.toString();
                String JSessionId = actual.split("JSESSIONID=")[1].split(" \r\n")[0];
                String expected = "HTTP/1.1 302 Found \r\n" +
                        "Content-Type: text/html;charset=utf-8 \r\n" +
                        "Content-Length: " + bytes.length + " \r\n" +
                        "Location: http://localhost:8080/index.html" + " \r\n" +
                        "Set-Cookie: JSESSIONID=" + JSessionId + " \r\n" +
                        "\r\n" +
                        new String(bytes);

                assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("실패 : login 시도 실패 시 예외 발생")
            void doGetSuccessContainParameterFail() {
                HttpRequest request = new HttpRequest(
                        new RequestLine("GET /login?account=kyummi&password=password HTTP/1.1"),
                        new RequestHeader(),
                        new RequestBody()
                );
                HttpResponse response = HttpResponse.of(request);
                LoginController loginController = new LoginController();

                assertThatThrownBy(() -> loginController.doGet(request, response))
                        .isInstanceOf(IllegalStateException.class)
                        .hasMessage("로그인 정보가 잘못되었습니다.");
            }
        }
    }
}
