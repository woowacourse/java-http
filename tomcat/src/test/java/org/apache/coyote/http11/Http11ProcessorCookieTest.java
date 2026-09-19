package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorCookieTest {

    @Test
    void JSESSIONID가_없으면_새로운_쿠키를_응답한다() {
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output())
                .containsPattern(
                        "Set-Cookie: JSESSIONID=[0-9a-f]{8}-[0-9a-f]{4}-"
                                + "[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
                );
    }

    @Test
    void 다른_쿠키만_있으면_새로운_JSESSIONID를_응답한다() {
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: yummy_cookie=choco",
                "",
                "");
        final var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output())
                .containsPattern(
                        "Set-Cookie: JSESSIONID=[0-9a-f]{8}-[0-9a-f]{4}-"
                                + "[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
                );
    }

    @Test
    void 기존_JSESSIONID가_있으면_새로운_쿠키를_응답하지_않는다() {
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=existing-session-id",
                "",
                "");
        final var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).doesNotContain("Set-Cookie: JSESSIONID");
    }
}
