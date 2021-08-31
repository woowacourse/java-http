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
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.service.StaticResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private static final String NEW_LINE = System.getProperty("line.separator");

    private RegisterController registerController;
    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        HashMap<String, User> database = new HashMap<>();
        userRepository = new InMemoryUserRepository(database, 1L);

        RegisterService registerService = new RegisterService(userRepository);
        StaticResourceService staticResourceService = new StaticResourceService();

        registerController = new RegisterController(registerService, staticResourceService);
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
            void findRegisterHtmlSuccess() throws IOException {
                // given
                String requestString = String.join(NEW_LINE, "GET /register HTTP/1.1", "", "");
                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }

                String expectString = "HTTP/1.1 200 OK \n"
                    + "Content-Length: 16 \n"
                    + "Content-Type: text/html; charset=UTF-8 \n"
                    + "\n"
                    + "register is good";

                // when
                HttpResponse httpResponse = registerController.service(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(
                    expectString.getBytes(StandardCharsets.UTF_8));
            }

            @DisplayName("요청한 파일이 없으면 '404.html' response를 반환 받는다.")
            @Test
            void findRegisterHtmlFail() throws IOException {
                // given
                String requestString = String.join(NEW_LINE, "GET /register/something HTTP/1.1", "",
                    "");
                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }

                String expectString = "HTTP/1.1 404 Not Found \n"
                    + "Content-Length: 9 \n"
                    + "Content-Type: text/html; charset=UTF-8 \n"
                    + "\n"
                    + "NOT FOUND";

                // when
                HttpResponse httpResponse = registerController.service(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(
                    expectString.getBytes(StandardCharsets.UTF_8));
            }
        }

        @DisplayName("POST 요청")
        @Nested
        class Post {

            private static final String ACCOUNT = "account";
            private static final String PASSWORD = "password";
            private static final String EMAIL = "email";

            private HttpRequest httpRequest;

            @BeforeEach
            void setUp() throws IOException {
                String body = String.format("account=%s&password=%s&email=%s", ACCOUNT, PASSWORD,
                    EMAIL);
                int contentLength = body.getBytes(StandardCharsets.UTF_8).length;

                String requestString = String.join(NEW_LINE,
                    "POST /register HTTP/1.1",
                    String.format("Content-Length: %d", contentLength),
                    "",
                    body
                );

                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }
            }

            @DisplayName("회원가입 성공시 '/index.html' redirect response를 반환 받는다.")
            @Test
            void registerSuccess() throws IOException {
                // given
                String expectString = "HTTP/1.1 301 Moved Permanently \n"
                    + "Location: /index.html ";

                // when
                HttpResponse httpResponse = registerController.service(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(
                    expectString.getBytes(StandardCharsets.UTF_8));
            }

            @DisplayName("회원가입 실패시 '/409.html' redirect response를 반환 받는다.")
            @Test
            void registerFail() throws IOException {
                // given
                userRepository.save(new User(ACCOUNT, PASSWORD, EMAIL));

                String expectString = "HTTP/1.1 409 Conflict \n"
                    + "Location: /409.html ";

                // when
                HttpResponse httpResponse = registerController.service(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(
                    expectString.getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}