package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.catalina.servlet.Controller;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpMessageUtils;

@DisplayName("/login 경로에 대해")
class LoginControllerTest {

    private final InMemoryUserRepository userRepository = new InMemoryUserRepository();
    private final Controller loginController = new LoginController(new UserService(userRepository));

    @DisplayName("GET 메서드의 요청은 해당 세션에 사용자 정보가 없는 경우 상태코드 200과 login.html 파일을 응답한다")
    @Test
    void get_notLoggedIn() throws IOException {
        final var httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var response = new HttpResponse();

        loginController.service(HttpMessageUtils.toRequest(httpRequest), response);

        final var actual = response.toMessage();
        assertAll(
                () -> assertThat(actual).startsWith("HTTP/1.1 200 OK \r\n"),
                () -> assertThat(actual).contains("Content-Type: text/html;charset=utf-8 \r\n"),
                () -> assertThat(actual).contains("Content-Length: 3797 \r\n"),
                () -> assertThat(actual).endsWith(HttpMessageUtils.getResponseBody("login.html"))
        );
    }

    @DisplayName("GET 메서드의 요청은 해당 세션에 사용자 정보가 있는 경우 상태코드 302와 Location 헤더 값으로 /index.html 경로를 응답한다")
    @Test
    void get_loggedIn() throws IOException {
        final var httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "COOKIE: JSESSION=asdasdasdfdg ",
                "",
                "");
        final var request = HttpMessageUtils.toRequest(httpRequest);
        final var continuedSession = request.getSession();
        continuedSession.setAttribute("user", new User(2L, "loggedInUser", "password", "abc@gmail.com"));
        final var response = new HttpResponse();

        loginController.service(request, response);

        final var actual = response.toMessage();
        assertAll(
                () -> assertThat(actual).startsWith("HTTP/1.1 302 Found \r\n"),
                () -> assertThat(actual).contains("Location: /index.html \r\n"),
                () -> assertThat(continuedSession.hasAttribute("user")).isTrue()
        );
    }

    @DisplayName("POST 메서드의 요청은 로그인에 성공하는 경우 해당 세션에 user 값을 할당하고, 상태코드 302와 Location 헤더 값으로 /index.html 경로를 응답한다")
    @Test
    void post_success() throws IOException {
        final String validAuthInfoRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "COOKIE: JSESSION=asdasdasdfdg ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 30 ",
                "",
                "account=gugu&password=password");
        final var request = HttpMessageUtils.toRequest(validAuthInfoRequest);
        HttpResponse response = new HttpResponse();

        loginController.service(request, response);

        final var actual = response.toMessage();
        final var updatedSession = request.getSession();
        updatedSession.hasAttribute("user");
        assertAll(
                () -> assertThat(actual).startsWith("HTTP/1.1 302 Found \r\n"),
                () -> assertThat(actual).contains("Location: /index.html \r\n"),
                () -> assertThat(updatedSession.hasAttribute("user")).isTrue()
        );
    }

    @DisplayName("POST 메서드의 요청은 로그인에 실패하는 경우 상태코드 401과 401.html 파일을 응답한다")
    @Test
    void post_fail() throws IOException {
        final String invalidAuthInfoRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "COOKIE: JSESSION=asdasdasdfdg ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 27 ",
                "",
                "account=wrong&password=wrong");
        final var request = HttpMessageUtils.toRequest(invalidAuthInfoRequest);
        HttpResponse response = new HttpResponse();

        loginController.service(request, response);

        final var actual = response.toMessage();
        final var updatedSession = request.getSession();
        updatedSession.hasAttribute("user");
        assertAll(
                () -> assertThat(actual).startsWith("HTTP/1.1 401 Unauthorized \r\n"),
                () -> assertThat(actual).contains("Content-Type: text/html;charset=utf-8 \r\n"),
                () -> assertThat(actual).contains("Content-Length: 2426 \r\n"),
                () -> assertThat(actual).endsWith(HttpMessageUtils.getResponseBody("401.html")),
                () -> assertThat(updatedSession.hasAttribute("user")).isFalse()
        );
    }
}
