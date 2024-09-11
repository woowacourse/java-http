package org.apache.coyote.http11.component.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.component.exception.AuthenticationException;
import org.apache.coyote.http11.component.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    @Test
    @DisplayName("로그인에 성공하면 index.html로 302 redirect하는 HttpResponse를 반환한다.")
    void return_http_response_that_has_302_status_code_with_location_index_html() {
        // given
        final var request = new HttpRequest(
                String.join("\r\n",
                        "GET /login?account=gugu&password=password HTTP/1.1 ",
                        "Host: http://localhost:8080",
                        "")
        );
        final var loginHandler = new LoginHandler("/login");

        // when
        final var response = loginHandler.handle(request);

        // then
        assertThat(response.getResponseText())
                .startsWith(String.join("\r\n",
                        "HTTP/1.1 302 FOUND ",
                        "Location: http://localhost:8080/index.html"));
    }

    @Test
    @DisplayName("로그인에 성공하면 Session의 아이디 쿠키를 발생한다.")
    void publish_session_id_to_cookie() {
        // given
        final var request = new HttpRequest(
                String.join("\r\n",
                        "GET /login?account=gugu&password=password HTTP/1.1 ",
                        "Host: http://localhost:8080",
                        "")
        );
        final var loginHandler = new LoginHandler("/login");

        // when
        final var response = loginHandler.handle(request);

        // then
        assertThat(response.getResponseText())
                .contains("Set-Cookie: JSESSIONID");
    }

    @Test
    @DisplayName("없는 아이디로 로그인 요청 시 예외를 발생시킨다.")
    void throw_exception_when_login_with_does_not_exist_account() {
        // given
        final var request = new HttpRequest(
                String.join("\r\n",
                        "GET /login?account=gugu1&password=password HTTP/1.1 ",
                        "Host: http://localhost:8080",
                        "")
        );
        final var loginHandler = new LoginHandler("/login");

        // when & then
        assertThatThrownBy(() -> loginHandler.handle(request))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    @DisplayName("틀린 비밀번호로 로그인 요청 시 예외를 발생시킨다.")
    void throw_exception_when_login_with_incorrect_password() {
        // given
        final var request = new HttpRequest(
                String.join("\r\n",
                        "GET /login?account=gugu1&password=password1 HTTP/1.1 ",
                        "Host: http://localhost:8080",
                        "")
        );
        final var loginHandler = new LoginHandler("login");

        // when & then
        assertThatThrownBy(() -> loginHandler.handle(request))
                .isInstanceOf(AuthenticationException.class);
    }
}
