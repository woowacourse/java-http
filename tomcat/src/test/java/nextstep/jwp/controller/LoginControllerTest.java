package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestURI;
import org.apache.coyote.http11.HttpStatusCode;
import org.apache.coyote.http11.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

class LoginControllerTest {

    @Nested
    class Login {

        @DisplayName("로그인에 성공한다.")
        @Test
        void loginSuccess() {
            // given
            final HttpHeaders httpHeaders = new HttpHeaders(Map.of(
                HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded"
            ));

            final HttpRequest httpRequest = new HttpRequest(httpHeaders, HttpMethod.POST, HttpRequestURI.from("/login"),
                "HTTP/1.1", Map.of(
                "account", "gugu", "password", "password"
            ));

            // when
            final LoginController loginController = new LoginController();
            try (final MockedConstruction<Session> sessionMockedConstruction = mockConstruction(Session.class,
                (mock, context) -> when(mock.getId()).thenReturn("1234"))
            ) {
                final ResponseEntity responseEntity = loginController.service(httpRequest);

                // then
                assertAll(
                    () -> assertThat(responseEntity.getResponseLine().getHttpStatus()).isEqualTo(HttpStatusCode.FOUND),
                    () -> assertThat(
                        responseEntity.getHeaders().containsHeaderNameAndValue(HttpHeaderName.LOCATION, "/index.html"))
                        .isTrue(),
                    () -> assertThat(responseEntity.getHeaders()
                        .containsHeaderNameAndValue(HttpHeaderName.SET_COOKIE, "JSESSIONID=1234")).isTrue()
                );
            }
        }

        @DisplayName("비밀번호가 다르면 로그인에 실패한다.")
        @Test
        void loginFailInvalidPassword() {
            // given
            final HttpHeaders httpHeaders = new HttpHeaders(Map.of(
                HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded"
            ));

            final HttpRequest httpRequest = new HttpRequest(httpHeaders, HttpMethod.POST, HttpRequestURI.from("/login"),
                "HTTP/1.1", Map.of(
                "account", "gugu", "password", "wrong"
            ));

            // when
            final LoginController loginController = new LoginController();
            final ResponseEntity responseEntity = loginController.service(httpRequest);

            // then
            assertAll(
                () -> assertThat(responseEntity.getResponseLine().getHttpStatus()).isEqualTo(HttpStatusCode.FOUND),
                () -> assertThat(
                    responseEntity.getHeaders().containsHeaderNameAndValue(HttpHeaderName.LOCATION, "/401.html")
                ).isTrue()
            );
        }

        @DisplayName("계정이 존재하지 않으면 로그인에 실패한다.")
        @Test
        void loginFailNotExistAccount() {
            // given
            final HttpHeaders httpHeaders = new HttpHeaders(Map.of(
                HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded"
            ));

            final HttpRequest httpRequest = new HttpRequest(httpHeaders, HttpMethod.POST, HttpRequestURI.from("/login"),
                "HTTP/1.1", Map.of(
                "account", "vero", "password", "password"
            ));

            // when
            final LoginController loginController = new LoginController();
            final ResponseEntity responseEntity = loginController.service(httpRequest);

            // then
            assertAll(
                () -> assertThat(responseEntity.getResponseLine().getHttpStatus()).isEqualTo(HttpStatusCode.FOUND),
                () -> assertThat(
                    responseEntity.getHeaders().containsHeaderNameAndValue(HttpHeaderName.LOCATION, "/401.html")
                ).isTrue()
            );
        }
    }

    @DisplayName("메서드가 POST, 경로가 /login 일 때 true 를 리턴한다.")
    @Test
    void canHandleMatchPathAndMethod() {
        // given
        final HttpHeaders httpHeaders = new HttpHeaders(Map.of(
            HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded"
        ));

        // when
        final HttpRequest httpRequest = new HttpRequest(httpHeaders, HttpMethod.POST, HttpRequestURI.from("/login"),
            "HTTP/1.1", Map.of(
            "account", "vero", "password", "password"
        ));

        // then
        final LoginController loginController = new LoginController();
        assertThat(loginController.canHandle(httpRequest)).isTrue();
    }
}
