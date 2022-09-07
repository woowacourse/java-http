package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.HttpBody;
import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthControllerTest {

    @DisplayName("사용자 회원가입이 정상적으로 처리된다.")
    @Test
    void userRegister() {
        final AuthController authController = new AuthController();

        final String requestLine = "POST /register HTTP/1.1";
        final HttpHeader httpHeader = new HttpHeader(requestLine,
                String.join("\r\n",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: keep-alive "));
        final HttpBody httpBody = new HttpBody("account=green&email=green@0wooteco.com&password=1234");

        authController.service(new HttpRequest(requestLine, httpHeader, httpBody), new HttpResponse());

        final User user = InMemoryUserRepository.findByAccount("green").get();

        assertThat(user.getAccount()).isEqualTo("green");
        assertThat(user.checkPassword("1234")).isTrue();
    }

    @DisplayName("사용자 로그인이 정상적으로 처리된다.")
    @Test
    void userLogin() {
        InMemoryUserRepository.save(new User("green", "1234", "green@0wooteco.com&"));

        final AuthController authController = new AuthController();

        final String requestLine = "POST /login.html HTTP/1.1";
        final HttpHeader httpHeader = new HttpHeader(requestLine,
                String.join("\r\n",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: keep-alive "));
        final HttpBody httpBody = new HttpBody("account=green&email=green@0wooteco.com&password=1234");

        final ResponseEntity responseEntity = authController.service(new HttpRequest(requestLine, httpHeader, httpBody),
                new HttpResponse());

        assertThat(responseEntity.getStatusCode()).isEqualTo(StatusCode.MOVED_TEMPORARILY);
    }
}
