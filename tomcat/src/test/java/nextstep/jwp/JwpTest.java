package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.apache.coyote.http11.model.Header;
import org.apache.coyote.http11.model.request.HttpRequest;
import org.apache.coyote.http11.model.response.HttpResponse;
import org.apache.coyote.http11.model.response.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwpTest {

    @DisplayName("경로가 '/'일 시, hello.txt 반환")
    @Test
    void hello() {
        final var request = HttpRequest.from(
                "GET / HTTP/1.1 ",
                List.of("Host: localhost:8080 ",
                        "Connection: keep-alive ")
        );

        HttpResponse response = RequestHandler.process(request);

        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(Status.OK),
                () -> assertThat(Integer.parseInt(response.getHeaderValue(Header.CONTENT_LENGTH)))
                        .isEqualTo("Hello world!".getBytes().length)
        );
    }

    @DisplayName("올바른 로그인 요청 시 Status Found 및 Location 반환")
    @Test
    void loginSuccess() {
        final var request = HttpRequest.from(
                "POST /login HTTP/1.1 ",
                List.of("Host: localhost:8080 ",
                        "Connection: keep-alive ")
        );
        request.addBody("account=gugu&password=password");

        HttpResponse response = RequestHandler.process(request);

        assertAll(
                () -> assertThat(response.getStatus()).isEqualTo(Status.FOUND),
                () -> assertThat(response.getHeaderValue(Header.LOCATION)).isEqualTo("/index.html")
        );
    }

    @DisplayName("존재하지 않는 유저 로그인 시 Status 401 반환")
    @Test
    void loginFail_idDoesNotExist_statusUnauthorized() {
        final var invalidRequest = HttpRequest.from(
                "POST /login HTTP/1.1 ",
                List.of("Host: localhost:8080 ",
                        "Connection: keep-alive ")
        );
        invalidRequest.addBody("account=brown&password=password");

        HttpResponse response = RequestHandler.process(invalidRequest);

        assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED);
    }

    @DisplayName("틀린 비밀번호로 로그인 시 Status 401 반환")
    @Test
    void loginFail_passwordNotMatch_statusUnauthorized() {
        final var invalidRequest = HttpRequest.from(
                "POST /login HTTP/1.1 ",
                List.of("Host: localhost:8080 ",
                        "Connection: keep-alive ")
        );
        invalidRequest.addBody("account=brown&password=wrongpassword");

        HttpResponse response = RequestHandler.process(invalidRequest);

        assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED);
    }

    @DisplayName("로그인 시 쿠키에 세션 id가 없다면, 응답 헤더에 Set-Cookie 설정")
    @Test
    void login_noSessionIdInRequestCookie_setHeaderCookie() {
        final var request = HttpRequest.from(
                "POST /login HTTP/1.1 ",
                List.of("Host: localhost:8080 ",
                        "Connection: keep-alive ")
        );
        request.addBody("account=gugu&password=password");

        final var response = RequestHandler.process(request);
        final var sessionId = response.getHeaderValue(Header.SET_COOKIE);

        assertThat(sessionId).isNotNull();
    }
}
