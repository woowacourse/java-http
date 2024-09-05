package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.Header;
import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class LoginHandlerTest {

    @Test
    @DisplayName("로그인 관련 요청을 처리할 수 있다.")
    void canHandle() {
        LoginHandler loginHandler = new LoginHandler();

        boolean result = loginHandler.canHandle(createHttpRequest("GET /login HTTP/1.1"));

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("로그인 관련 요청이 아니라면 처리할 수 없다.")
    void cantHandle() {
        LoginHandler loginHandler = new LoginHandler();

        boolean result = loginHandler.canHandle(createHttpRequest("GET /login1?a=1 HTTP/1.1"));

        assertThat(result).isFalse();
    }

    private HttpRequest createHttpRequest(String startLine) {
        return new HttpRequest(startLine, new Header(Collections.emptyList()));
    }
}
