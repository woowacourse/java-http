package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void getHeader_success() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                ""
        );

        final BufferedReader reader = new BufferedReader(
                new StringReader(httpRequest)
        );

        // when
        final HttpRequest request = new HttpRequest(reader);

        // then
        assertThat(request.getHeader("Host"))
                .isEqualTo("localhost:8080");
        assertThat(request.getHeader("Connection"))
                .isEqualTo("keep-alive");
    }

    @Test
    void requestLine_success() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /users?id=1 HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );

        final BufferedReader reader = new BufferedReader(
                new StringReader(httpRequest)
        );

        // when
        final HttpRequest request = new HttpRequest(reader);

        // then
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getUri()).isEqualTo("/users?id=1");
        assertThat(request.getPath()).isEqualTo("/users");
        assertThat(request.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void getBody_success() {
        // given
        final String body = "account=gugu&password=1234";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        );
        final BufferedReader reader = new BufferedReader(
                new StringReader(httpRequest)
        );

        // when
        final HttpRequest request = new HttpRequest(reader);

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

        final HttpRequest request = new HttpRequest(
                new BufferedReader(
                        new StringReader(httpRequest)
                )
        );

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

        // when
        final HttpRequest request = new HttpRequest(
                new BufferedReader(
                        new StringReader(httpRequest)
                )
        );

        // then
        assertThat(request.getSession()).isNotNull();
        assertThat(request.isNewSession()).isTrue();

        assertThat(
                SessionManager.getInstance()
                        .findSession(request.getSession().getId())
        ).isSameAs(request.getSession());
    }
}
