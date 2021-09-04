package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.server.HttpSessions;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.StaticResourceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";

    private LoginController loginController;
    private InMemoryUserRepository userRepository;
    private HttpRequest httpRequest;

    @BeforeEach
    void setUp() {
        HashMap<String, User> database = new HashMap<>();
        userRepository = new InMemoryUserRepository(database, new AtomicLong(1));

        HttpSessions httpSessions = new HttpSessions();
        LoginService loginService = new LoginService(userRepository, httpSessions);
        StaticResourceService staticResourceService = new StaticResourceService();

        loginController = new LoginController(loginService, staticResourceService);
    }

    @DisplayName("HttpRequest Method에 따라 Service 분기")
    @Nested
    class Service {

        @DisplayName("GET 요청")
        @Nested
        class Get {

            @DisplayName("요청한 파일이 존재하면 관련 response를 반환 받는다.")
            @Test
            void findLoginHtmlSuccess() throws IOException {
                // given
                String requestString = String.join(NEW_LINE, "GET /login HTTP/1.1", "", "");
                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }

                String expectString = "HTTP/1.1 200 OK \n"
                    + "Content-Length: 13 \n"
                    + "Content-Type: text/html; charset=UTF-8 \n"
                    + "\n"
                    + "login is good";

                // when
                HttpResponse httpResponse = loginController.service(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(
                    expectString.getBytes(StandardCharsets.UTF_8));
            }

            @DisplayName("이미 로그인 되어 있을 경우 '/index.html' redirect response를 반환 받는다.")
            @Test
            void alreadyLogin() throws IOException {
                // given
                userRepository.save(new User(ACCOUNT, PASSWORD, "email"));

                getLoginRequest();
                HttpResponse httpResponse = loginController.service(httpRequest);
                getReLoginRequest(httpResponse);

                String expectStatusLine = "HTTP/1.1 302 Found \n"
                    + "Location: /index.html ";

                // when
                HttpResponse response = loginController.doPost(httpRequest);

                // then
                assertThat(response.toBytes()).isEqualTo(
                    expectStatusLine.getBytes(StandardCharsets.UTF_8));
            }

            private void getLoginRequest() throws IOException {
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

            private void getReLoginRequest(HttpResponse httpResponse) throws IOException {
                String cookie = getParsingCookie(httpResponse);

                String body = String.format("account=%s&password=%s", ACCOUNT, PASSWORD);
                int contentLength = body.getBytes(StandardCharsets.UTF_8).length;

                String requestString = String.join(NEW_LINE,
                    "GET /login HTTP/1.1",
                    String.format("Cookie: %s", cookie),
                    String.format("Content-Length: %d", contentLength),
                    "",
                    body
                );

                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }
            }

            private String getParsingCookie(HttpResponse httpResponse) {
                String CookieHeader = httpResponse.toString().split("\n")[1];
                String cookieValue = CookieHeader.split(":")[1];

                return cookieValue.trim();
            }

            @DisplayName("요청한 파일이 없으면 '404.html' response를 반환 받는다.")
            @Test
            void findLoginHtmlFail() throws IOException {
                // given
                String requestString = String.join(NEW_LINE, "GET /login/something HTTP/1.1", "",
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
                HttpResponse httpResponse = loginController.service(httpRequest);

                // then
                assertThat(httpResponse.toBytes()).isEqualTo(
                    expectString.getBytes(StandardCharsets.UTF_8));
            }
        }

        @DisplayName("POST 요청")
        @Nested
        class Post {

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

            @DisplayName("로그인 성공시 쿠키와 함께 '/index.html' response를 반환 받는다.")
            @Test
            void loginSuccess() throws IOException {
                // given
                userRepository.save(new User(ACCOUNT, PASSWORD, "email"));

                String expectStatusLine = "HTTP/1.1 200 OK ";
                String expectCookie = "Set-Cookie:";
                String expectContentLength = "Content-Length: 11 ";
                String expectContentType = "Content-Type: text/html; charset=UTF-8 ";
                String expectEnterLine = "";
                String expectBody = "hihi hello!";

                // when
                HttpResponse httpResponse = loginController.service(httpRequest);
                String response = new String(httpResponse.toBytes());
                String[] splitedResponse = response.split("\n");

                // then
                assertThat(splitedResponse[0]).isEqualTo(expectStatusLine);
                assertThat(splitedResponse[1].startsWith(expectCookie)).isTrue();
                assertThat(splitedResponse[2]).isEqualTo(expectContentLength);
                assertThat(splitedResponse[3]).isEqualTo(expectContentType);
                assertThat(splitedResponse[4]).isEqualTo(expectEnterLine);
                assertThat(splitedResponse[5]).isEqualTo(expectBody);
            }

            @DisplayName("이미 로그인 되어 있을 경우 '/index.html' redirect response를 반환 받는다.")
            @Test
            void alreadyLogin() throws IOException {
                // given
                userRepository.save(new User(ACCOUNT, PASSWORD, "email"));

                HttpResponse httpResponse = loginController.service(httpRequest);
                String cookie = getParsingCookie(httpResponse);

                String body = String.format("account=%s&password=%s", ACCOUNT, PASSWORD);
                int contentLength = body.getBytes(StandardCharsets.UTF_8).length;

                String requestString = String.join(NEW_LINE,
                    "POST /login HTTP/1.1",
                    String.format("Cookie: %s", cookie),
                    String.format("Content-Length: %d", contentLength),
                    "",
                    body
                );

                try (InputStream inputStream = new ByteArrayInputStream(requestString.getBytes(
                    StandardCharsets.UTF_8))) {
                    httpRequest = HttpRequest.parse(inputStream);
                }

                String expectStatusLine = "HTTP/1.1 302 Found \n"
                    + "Location: /index.html ";

                // when
                HttpResponse response = loginController.doPost(httpRequest);

                // then
                assertThat(response.toBytes()).isEqualTo(
                    expectStatusLine.getBytes(StandardCharsets.UTF_8));
            }

            private String getParsingCookie(HttpResponse httpResponse) {
                String CookieHeader = httpResponse.toString().split("\n")[1];
                String cookieValue = CookieHeader.split(":")[1];

                return cookieValue.trim();
            }

            @DisplayName("로그인 실패시 '/401.html' response를 반환 받는다.")
            @Test
            void loginFail() throws IOException {
                // given
                String expectStatusLine = "HTTP/1.1 401 Unauthorized ";
                String expectContentLength = "Content-Length: 20 ";
                String expectContentType = "Content-Type: text/html; charset=UTF-8 ";
                String expectEnterLine = "";
                String expectBody = "401 is Unauthorized.";

                // when
                HttpResponse httpResponse = loginController.service(httpRequest);
                String[] splitedResponse = httpResponse.toString().split("\n");

                // then
                assertThat(splitedResponse[0]).isEqualTo(expectStatusLine);
                assertThat(splitedResponse[1]).isEqualTo(expectContentLength);
                assertThat(splitedResponse[2]).isEqualTo(expectContentType);
                assertThat(splitedResponse[3]).isEqualTo(expectEnterLine);
                assertThat(splitedResponse[4]).isEqualTo(expectBody);
            }
        }
    }
}