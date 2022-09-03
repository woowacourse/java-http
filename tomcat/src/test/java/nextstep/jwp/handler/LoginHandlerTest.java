package nextstep.jwp.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    @DisplayName(value = "쿼리 스트링이 없는 경우 200 반환")
    @Test
    void login_page() {
        // given
        final HttpRequest request = new HttpRequest("/login");
        final LoginHandler loginHandler = new LoginHandler();

        final String expected = "HTTP/1.1 200 OK ";

        // when
        final HttpResponse response = loginHandler.login(request);

        // then
        assertThat(response.generateResponse()).contains(expected);
    }

    @DisplayName(value = "로그인 성공 시 302 반환")
    @Test
    void login_success() {
        // given
        final HttpRequest request = new HttpRequest("/login?account=gugu&password=password");
        final LoginHandler loginHandler = new LoginHandler();

        final String expectedStatusCode = "HTTP/1.1 302";
        final String expectedLocation = "Location: /index.html ";

        // when
        final HttpResponse response = loginHandler.login(request);
        final String actual = response.generateResponse();

        // then
        assertThat(actual).contains(expectedStatusCode);
        assertThat(actual).contains(expectedLocation);
    }

    @DisplayName(value = "로그인 실패하는 경우 401 반환")
    @Test
    void login_failed() {
        // given
        final HttpRequest request = new HttpRequest("/login?account=gugu&password=notPassword");
        final LoginHandler loginHandler = new LoginHandler();

        final String expected = "HTTP/1.1 401";

        // when
        final HttpResponse response = loginHandler.login(request);

        // then
        assertThat(response.generateResponse()).contains(expected);
    }
}
