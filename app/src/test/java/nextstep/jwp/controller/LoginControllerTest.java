package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.StaticResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private LoginController loginController;
    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        HashMap<String, User> database = new HashMap<>();
        userRepository = new InMemoryUserRepository(database, 1L);

        LoginService loginService = new LoginService(userRepository);
        StaticResourceService staticResourceService = new StaticResourceService();

        loginController = new LoginController(loginService, staticResourceService);
    }

    @DisplayName("'/login'으로 시작되는 Uri와 매칭된다.")
    @Test
    void matchUri() {
        // when
        boolean matchResult1 = loginController.matchUri("/login");
        boolean matchResult2 = loginController.matchUri("/login/wow");
        boolean matchResult3 = loginController.matchUri("/login?abc=edf");

        // then
        assertThat(matchResult1).isTrue();
        assertThat(matchResult2).isTrue();
        assertThat(matchResult3).isTrue();
    }

    @DisplayName("HttpRequest Method에 따라 Service 분기")
    @Nested
    class Service {

        @DisplayName("GET 요청")
        @Nested
        class Get {

            private HttpRequest httpRequest;

            @DisplayName("요청한 파일이 존재하면 관련 response를 반환 받는다.")
            @Test
            void findLoginHtmlSuccess() throws IOException {
                // given
                String requestString = String.join(NEW_LINE, "GET /login HTTP/1.1", "", "");
                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }

                String expectString = "\n\n"
                    + "HTTP/1.1 200 OK \n"
                    + "Content-Length: 13 \n"
                    + "Content-Type: text/html; charset=UTF-8 \n"
                    + "\n"
                    + "login is good";

                // when
                HttpResponse httpResponse = loginController.doService(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
            }

            @DisplayName("요청한 파일이 없으면 '404.html' response를 반환 받는다.")
            @Test
            void findLoginHtmlFail() throws IOException {
                // given
                String requestString = String.join(NEW_LINE, "GET /login/something HTTP/1.1", "", "");
                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }

                String expectString = "\n\n"
                    + "HTTP/1.1 404 Not Found \n"
                    + "Content-Length: 9 \n"
                    + "Content-Type: text/html; charset=UTF-8 \n"
                    + "\n"
                    + "NOT FOUND";

                // when
                HttpResponse httpResponse = loginController.doService(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
            }
        }

        @DisplayName("POST 요청")
        @Nested
        class Post {

            private static final String ACCOUNT = "account";
            private static final String PASSWORD = "password";

            private HttpRequest httpRequest;

            @BeforeEach
            void setUp() throws IOException {
                String body = String.format("account=%s&password=%s", ACCOUNT, PASSWORD);
                int contentLength = body.getBytes(StandardCharsets.UTF_8).length;

                String requestString = String.join(NEW_LINE,
                    "POST /login HTTP/1.1",
                    String.format("Content-Length: %d", contentLength),
                    "",
                    body
                );

                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }
            }

            @DisplayName("로그인 성공시 '/index.html' redirect response를 반환 받는다.")
            @Test
            void loginSuccess() throws IOException {
                // given
                userRepository.save(new User(ACCOUNT, PASSWORD, "email"));

                String expectString = "\n\n"
                    + "HTTP/1.1 302 Found \n"
                    + "Location: /index.html ";

                // when
                HttpResponse httpResponse = loginController.doService(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
            }

            @DisplayName("로그인 실패시 '/401.html' redirect response를 반환 받는다.")
            @Test
            void loginFail() throws IOException {
                // given
                String expectString = "\n\n"
                    + "HTTP/1.1 401 Unauthorized \n"
                    + "Location: /401.html ";

                // when
                HttpResponse httpResponse = loginController.doService(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(expectString.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}