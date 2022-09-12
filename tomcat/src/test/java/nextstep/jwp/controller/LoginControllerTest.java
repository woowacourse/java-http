package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginControllerTest extends ControllerTest {

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.clear();
    }

    @DisplayName("로그인에 성공하면 302, index.html을 응답한다.")
    @Test
    void login_success() throws Exception {
        // given
        InMemoryUserRepository.save(new User("gugu", "password", "gugu@gmail.com"));
        String body = "account=gugu&password=password";
        String requestValue = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.length() + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                body);
        HttpRequest request = HttpRequest.from(toBufferedReader(requestValue));

        // when
        HttpResponse response = RequestMapping.from(request)
                .service(request);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeader("Location")).isEqualTo("/index.html");
    }

    @DisplayName("로그인에 실패하면 401, 401.html을 응답한다.")
    @Test
    void login_fail() throws Exception {
        // given
        String requestValue = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu&password=invalidPassword");
        HttpRequest request = HttpRequest.from(toBufferedReader(requestValue));

        // when
        HttpResponse response = RequestMapping.from(request)
                .service(request);

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeader("Location")).isEqualTo("/401.html");
    }
}
