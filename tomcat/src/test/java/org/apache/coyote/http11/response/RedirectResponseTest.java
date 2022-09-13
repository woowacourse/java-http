package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RedirectResponseTest {

    @Test
    @DisplayName("리다이렉트 응답을 반환하는 응답객체를 생성한다.")
    void of() {
        final HttpResponse redirectResponse = RedirectResponse.of("/index.html");

        final String httpMessage = redirectResponse.toHttpMessage();

        final String expected = "HTTP/1.1 302 Found \r\n"
                + "Content-Type: text/html;charset=utf-8 \r\n"
                + "Content-Length: 0 \r\n"
                + "Location: /index.html \r\n"
                + "\r\n";
        assertThat(httpMessage).isEqualTo(expected);
    }
}