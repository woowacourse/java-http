package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.model.request.Request;
import org.apache.coyote.http11.model.response.Response;
import org.apache.coyote.http11.model.response.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ControllerTest {

    @DisplayName("올바른 로그인 요청 시 Status Found 및 Location 반환")
    @Test
    void loginSuccess() {
        final var request = Request.from(
                String.join("\r\n",
                        "GET /login?account=gugu&password=password HTTP/1.1 ",
                        "Host: localhost:8080 ",
                        "Connection: keep-alive ",
                        "",
                        "")
        );

        Response response = Controller.process(request);

        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(Status.FOUND),
                () -> assertThat(response.getHeaderValue("Location")).isEqualTo("/index.html")
        );
    }

    @DisplayName("존재하지 않는 유저 로그인 시 Status 401 반환")
    @Test
    void loginFail_idDoesNotExist_statusUnauthorized() {
        final var invalidRequest = Request.from(
                String.join("\r\n",
                        "GET /login?account=brown&password=password HTTP/1.1 ",
                        "Host: localhost:8080 ",
                        "Connection: keep-alive ",
                        "",
                        "")
        );

        Response response = Controller.process(invalidRequest);

        assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED);
    }

    @DisplayName("틀린 비밀번호로 로그인 시 Status 401 반환")
    @Test
    void loginFail_passwordNotMatch_statusUnauthorized() {
        final var invalidRequest = Request.from(
                String.join("\r\n",
                        "GET /login?account=gugupassword=wrongpassword HTTP/1.1 ",
                        "Host: localhost:8080 ",
                        "Connection: keep-alive ",
                        "",
                        "")
        );

        Response response = Controller.process(invalidRequest);

        assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED);
    }
}
