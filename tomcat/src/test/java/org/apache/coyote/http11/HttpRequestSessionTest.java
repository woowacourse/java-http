package org.apache.coyote.http11;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestSessionTest {

    private final SessionManager sessionManager = SessionManager.getInstance();
    private String createdSessionId;

    @AfterEach
    void tearDown() {
        sessionManager.remove("existing-session-id");
        if (createdSessionId != null) {
            sessionManager.remove(createdSessionId);
        }
    }

    @Test
    void JSESSIONID에_해당하는_기존_세션을_반환한다() throws IOException {
        final Session existingSession = new Session("existing-session-id");
        sessionManager.add(existingSession);
        final HttpRequest request = requestWithCookie("JSESSIONID=existing-session-id");

        final Session session = request.getSession(false);

        assertThat(session).isSameAs(existingSession);
    }

    @Test
    void 세션이_없고_create가_false면_null을_반환한다() throws IOException {
        final HttpRequest request = requestWithoutCookie();

        final Session session = request.getSession(false);

        assertThat(session).isNull();
    }

    @Test
    void 세션이_없고_create가_true면_새로운_세션을_생성한다() throws IOException {
        final HttpRequest request = requestWithoutCookie();

        final Session session = request.getSession(true);
        createdSessionId = session.getId();

        assertThat(session.getId()).isNotBlank();
        assertThat(sessionManager.findSession(session.getId())).isSameAs(session);
    }

    private HttpRequest requestWithCookie(final String cookie) throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Cookie: " + cookie,
                "",
                "");
        return requestFrom(httpRequest);
    }

    private HttpRequest requestWithoutCookie() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "",
                "");
        return requestFrom(httpRequest);
    }

    private HttpRequest requestFrom(final String httpRequest) throws IOException {
        return HttpRequest.from(new ByteArrayInputStream(httpRequest.getBytes(StandardCharsets.UTF_8)));
    }
}
