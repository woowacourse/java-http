package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.service.UserService;
import org.apache.catalina.servlet.Controller;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpMessageUtils;

@DisplayName("/register 경로에 대해")
class RegisterControllerTest {

    private final InMemoryUserRepository userRepository = new InMemoryUserRepository();
    private final Controller registerController = new RegisterController(new UserService(userRepository));

    @DisplayName("GET 메서드의 요청은 상태코드 200과 register.html 파일을 응답한다")
    @Test
    void get() throws IOException {
        final var httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var response = new HttpResponse();

        registerController.service(HttpMessageUtils.toRequest(httpRequest), response);

        final var actual = response.toMessage();
        assertAll(
                () -> assertThat(actual).startsWith("HTTP/1.1 200 OK \r\n"),
                () -> assertThat(actual).contains("Content-Type: text/html;charset=utf-8 \r\n"),
                () -> assertThat(actual).contains("Content-Length: 4319 \r\n"),
                () -> assertThat(actual).endsWith(HttpMessageUtils.getResponseBody("register.html"))
        );
    }

    @DisplayName("POST 메서드의 요청은 회원가입에 성공하는 경우 상태코드 302와 Location 헤더 값으로 /index.html 경로를 응답한다")
    @Test
    void post_success() throws IOException {
        final String validNewUserRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "COOKIE: JSESSION=asdasdasdfdg ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 52 ",
                "",
                "account=kuku&password=password&email=asd@gmail.com");
        HttpResponse response = new HttpResponse();

        registerController.service(HttpMessageUtils.toRequest(validNewUserRequest), response);

        final var actual = response.toMessage();
        assertAll(
                () -> assertThat(actual).startsWith("HTTP/1.1 302 Found \r\n"),
                () -> assertThat(actual).contains("Location: /index.html \r\n")
        );
    }

    @DisplayName("POST 메서드의 요청은 계정 생성에 실패하는 경우 상태코드 400과 400.html 파일을 응답한다")
    @Test
    void post_fail() throws IOException {
        final String invalidAuthInfoRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "COOKIE: JSESSION=asdasdasdfdg ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 52 ",
                "",
                "account=gugu&password=password&email=asd@gmail.com");
        final var request = HttpMessageUtils.toRequest(invalidAuthInfoRequest);
        HttpResponse response = new HttpResponse();

        registerController.service(request, response);

        final var actual = response.toMessage();
        assertAll(
                () -> assertThat(actual).startsWith("HTTP/1.1 400 Bad Request \r\n"),
                () -> assertThat(actual).contains("Content-Type: text/html;charset=utf-8 \r\n"),
                () -> assertThat(actual).contains("Content-Length: 2011 \r\n"),
                () -> assertThat(actual).endsWith(HttpMessageUtils.getResponseBody("400.html"))
        );
    }
}
