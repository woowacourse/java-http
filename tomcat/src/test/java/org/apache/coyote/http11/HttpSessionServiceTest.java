package org.apache.coyote.http11;

import jakarta.servlet.http.HttpSession;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionServiceTest {

    private final SessionManager sessionManager =
            SessionManager.getInstance();

    private final HttpSessionService sessionService =
            new HttpSessionService(sessionManager);

    @Test
    void JSESSIONID가_없으면_쿠키를_발급하지만_세션은_생성하지_않는다()
            throws Exception {

        // given
        final HttpRequest request = createRequest(
                String.join(
                        "\r\n",
                        "GET / HTTP/1.1",
                        "Host: localhost:8080",
                        "",
                        ""
                )
        );

        final HttpResponse response = new HttpResponse();

        // when
        sessionService.ensureSessionIdCookie(request, response);

        response.ok("text/html;charset=utf-8", new byte[0]);

        final String result = writeResponse(response);

        final String sessionId = extractSessionId(result);

        // then
        assertThat(result).contains("Set-Cookie: JSESSIONID=");

        assertThat(sessionManager.findSession(sessionId)).isNull();
    }

    @Test
    void 요청의_JSESSIONID로_기존_세션을_조회한다()
            throws Exception {

        // given
        final HttpSession session = sessionManager.createSession();

        final HttpRequest request = createRequest(
                String.join(
                        "\r\n",
                        "GET /login HTTP/1.1",
                        "Host: localhost:8080",
                        "Cookie: JSESSIONID="
                                + session.getId(),
                        "",
                        ""
                )
        );

        // when
        final HttpSession foundSession = sessionService.findSession(request);

        // then
        assertThat(foundSession).isSameAs(session);
    }

    @Test
    void 세션을_생성하면_실제_세션_ID를_쿠키로_응답한다()
            throws Exception {

        // given
        final HttpResponse response = new HttpResponse();

        // when
        final HttpSession session = sessionService.createSession(response);

        response.ok("text/html;charset=utf-8", new byte[0]);

        final String result = writeResponse(response);

        // then
        assertThat(result)
                .contains("Set-Cookie: JSESSIONID=" + session.getId());

        assertThat(sessionManager.findSession(session.getId())).isSameAs(session);
    }

    @Test
    void 기존_세션이_있으면_무효화하고_새로운_세션으로_교체한다() throws Exception {

        // given
        final HttpSession existingSession = sessionManager.createSession();

        final String existingSessionId = existingSession.getId();

        final HttpRequest request =
                createRequest(String.join(
                                "\r\n",
                                "POST /login HTTP/1.1",
                                "Host: localhost:8080",
                                "Cookie: JSESSIONID="
                                        + existingSessionId,
                                "",
                                ""
                        )
                );

        final HttpResponse response = new HttpResponse();

        HttpSession newSession = null;

        try {
            // when
            newSession = sessionService.replaceSession(request, response);

            // then
            assertThat(sessionManager.findSession(existingSessionId)).isNull();

            assertThat(newSession.getId()).isNotEqualTo(existingSessionId);

            assertThat(sessionManager.findSession(newSession.getId())).isSameAs(newSession);

            response.ok("text/html;charset=utf-8", new byte[0]);

            final String result = writeResponse(response);

            assertThat(result).contains("Set-Cookie: JSESSIONID=" + newSession.getId());

        } finally {
            if (newSession != null) {
                newSession.invalidate();
            }
        }
    }


    private HttpRequest createRequest(final String rawRequest) throws Exception {

        return HttpRequest.from(new ByteArrayInputStream(
                rawRequest.getBytes(StandardCharsets.UTF_8))
        ).orElseThrow();
    }

    private String writeResponse(final HttpResponse response) throws Exception {

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        response.writeTo(outputStream);

        return outputStream.toString(StandardCharsets.UTF_8);
    }

    private String extractSessionId(final String response) {
        return response.lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .map(line -> line.substring("Set-Cookie: JSESSIONID=".length()).trim())
                .findFirst()
                .orElseThrow();
    }
}