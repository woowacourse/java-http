package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpMethod;
import org.apache.coyote.http11.enums.HttpStatus;
import org.apache.coyote.http11.handler.LoginRequestHandler;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LoginRequestHandlerTest {

    @Test
    void 올바른_계정과_비밀번호면_302를_반환한다() {
        // given
        final LoginRequestHandler loginRequestHandler =
                new LoginRequestHandler();

        final HttpRequest request = createLoginRequest("gugu", "password");

        // when
        final HttpResponse response = loginRequestHandler.handle(request);

        // then
        assertThat(response.httpStatus())
                .isEqualTo(HttpStatus.FOUND);
        assertThat(response.path())
                .isEqualTo("/index.html");
    }

    @Test
    void 비밀번호가_일치하지_않으면_401을_반환한다() {
        // given
        final LoginRequestHandler loginRequestHandler =
                new LoginRequestHandler();

        final HttpRequest request = createLoginRequest("gugu", "wrong-password");

        // when
        final HttpResponse response = loginRequestHandler.handle(request);

        // then
        assertThat(response.httpStatus())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.path())
                .isEqualTo("/401.html");
    }

    private HttpRequest createLoginRequest(
            final String account,
            final String password
    ) {
        return HttpRequest.builder()
                .httpMethod(HttpMethod.POST)
                .path("/login")
                .version("HTTP/1.1")
                .params(Map.of(
                        "account", account,
                        "password", password
                ))
                .build();
    }
}
