package org.apache.coyote.http11;

import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorCookieTest {

    private static final Pattern JSESSION_ID_PATTERN = Pattern.compile(
            "Set-Cookie: JSESSIONID=([0-9a-f-]+)"
    );

    private String createdSessionId;

    @AfterEach
    void tearDown() {
        if (createdSessionId != null) {
            SessionManager.getInstance().remove(createdSessionId);
        }
    }

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

        final Matcher matcher = JSESSION_ID_PATTERN.matcher(socket.output());
        assertThat(matcher.find()).isTrue();
        createdSessionId = matcher.group(1);
        assertThat(SessionManager.getInstance().findSession(createdSessionId)).isNotNull();
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
