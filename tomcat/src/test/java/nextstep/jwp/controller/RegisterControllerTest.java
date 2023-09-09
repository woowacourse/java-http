package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.apache.coyote.http11.HttpHeaderName;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestURI;
import org.apache.coyote.http11.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @DisplayName("회원 가입에 성공한다.")
    @Test
    void registerSuccess() {
        // given
        final HttpHeaders httpHeaders = new HttpHeaders(Map.of(
            HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded"
        ));

        final HttpRequest httpRequest = new HttpRequest(httpHeaders, HttpMethod.POST, HttpRequestURI.from("/register"),
            "HTTP/1.1", Map.of(
            "account", "vero", "password", "password", "email", "vero@vero"
        ));

        // when
        final RegisterController registerController = new RegisterController();
        final ResponseEntity responseEntity = registerController.service(httpRequest);

        // then
        assertAll(
            () -> assertThat(responseEntity.getResponseLine().getHttpStatus()).isEqualTo(HttpStatusCode.FOUND),
            () -> assertThat(
                responseEntity.getHeaders().containsHeaderNameAndValue(HttpHeaderName.LOCATION, "/index.html"))
                .isTrue()
        );
    }

    @DisplayName("메서드가 POST, 경로가 /register, Content-Type이 application/x-www-form-urlencoded 인 경우 요청을 처리한다.")
    @Test
    void canHandleMatchPathAndMethod() {
        // given
        final HttpHeaders httpHeaders = new HttpHeaders(Map.of(
            HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded"
        ));

        final HttpRequest httpRequest = new HttpRequest(httpHeaders, HttpMethod.POST, HttpRequestURI.from("/register"),
            "HTTP/1.1", Map.of(
            "account", "vero", "password", "password", "email", "vero@vero"
        ));

        // when
        final RegisterController registerController = new RegisterController();
        final ResponseEntity responseEntity = registerController.service(httpRequest);

        // then
        assertThat(responseEntity.getResponseLine().getHttpStatus()).isEqualTo(HttpStatusCode.FOUND);
    }
}
