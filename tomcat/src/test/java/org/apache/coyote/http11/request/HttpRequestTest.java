package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void getHeader_success() {
        // given
        final String httpRequest = String.join(
                "\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                ""
        );

        // when
        final HttpRequest request = createRequest(httpRequest);

        // then
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Connection")).isEqualTo("keep-alive");
    }

    @Test
    void requestLine_success() {
        // given
        final String httpRequest = String.join(
                "\r\n",
                "GET /users?id=1 HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );

        // when
        final HttpRequest request = createRequest(httpRequest);

        // then
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getUri()).isEqualTo("/users?id=1");
        assertThat(request.getPath()).isEqualTo("/users");
        assertThat(request.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void getBody_success() {
        // given
        final String body =
                "account=gugu&password=1234";

        final int contentLength =
                body.getBytes(StandardCharsets.UTF_8).length;

        final String httpRequest = String.join(
                "\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + contentLength,
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );

        // when
        final HttpRequest request =
                createRequest(httpRequest);

        // then
        assertThat(request.getBody().getRawBody())
                .isEqualTo(body);
    }

    @Test
    void getSession_existingSession_success() {
        // given
        final String sessionId = "existing-session-id";

        final Session session = new Session(sessionId);

        SessionManager.getInstance().add(session);

        final String httpRequest = String.join(
                "\r\n",
                "GET /login HTTP/1.1",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                ""
        );

        final HttpRequest request = createRequest(httpRequest);

        // when
        final Session result = request.getSession();

        // then
        assertThat(result).isSameAs(session);
    }

    @Test
    void getSession_withoutCookie_createSession_success() {
        // given
        final String httpRequest = String.join(
                "\r\n",
                "GET /login HTTP/1.1",
                "",
                ""
        );

        final HttpRequest request = createRequest(httpRequest);

        // when
        final Session session = request.getSession();

        // then
        assertThat(session).isNotNull();
        assertThat(request.isNewSession()).isTrue();

        assertThat(
                SessionManager.getInstance()
                        .findSession(session.getId())
        ).isSameAs(session);
    }

    private HttpRequest createRequest(
            final String value
    ) {
        return new HttpRequest(
                new HttpRequestInput(
                        new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8))
                )
        );
    }
}
