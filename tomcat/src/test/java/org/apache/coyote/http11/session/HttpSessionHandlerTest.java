package org.apache.coyote.http11.session;

import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpSessionHandlerTest {

    private final SessionManager sessionManager =
            SessionManager.getInstance();

    private final HttpSessionHandler sessionHandler =
            new HttpSessionHandler(sessionManager);

    @Test
    void JSESSIONID가_없으면_쿠키를_발급하지만_세션은_생성하지_않는다()
            throws Exception {

        // given
        final HttpRequest request = createRequest("");
        final HttpResponse response = new HttpResponse();

        // when
        sessionHandler.writeSessionCookie(request, response);

        final String result = writeResponse(response);

        // then
        assertThat(result).contains("Set-Cookie: JSESSIONID=");

        assertThat(sessionManager.findSession(extractSessionId(result))).isNull();
    }

    @Test
    void 요청_처리_중_세션이_생성되면_실제_세션_ID를_쿠키로_응답한다()
            throws Exception {

        // given
        final HttpRequest request = createRequest("");
        final HttpResponse response = new HttpResponse();

        final HttpSession session = request.getSession();

        try {
            // when
            sessionHandler.writeSessionCookie(request, response);

            // then
            assertThat(writeResponse(response))
                    .contains("Set-Cookie: JSESSIONID=" + session.getId());
        } finally {
            session.invalidate();
        }
    }

    @Test
    void 기존_세션을_사용하면_쿠키를_다시_발급하지_않는다()
            throws Exception {

        // given
        final HttpSession session = sessionManager.createSession();

        final HttpRequest request = createRequest("Cookie: JSESSIONID=" + session.getId());
        final HttpResponse response = new HttpResponse();

        try {
            request.getSession();

            // when
            sessionHandler.writeSessionCookie(request, response);

            // then
            assertThat(writeResponse(response)).doesNotContain("Set-Cookie: JSESSIONID=");
        } finally {
            session.invalidate();
        }
    }

    private HttpRequest createRequest(final String cookieHeader) throws Exception {
        final String rawRequest = String.join(
                "\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                cookieHeader,
                "",
                ""
        ).replace("\r\n\r\n\r\n", "\r\n\r\n");

        final HttpRequest request = HttpRequest.from(new ByteArrayInputStream(
                rawRequest.getBytes(StandardCharsets.UTF_8))
        ).orElseThrow();

        sessionHandler.prepare(request);

        return request;
    }

    private String writeResponse(final HttpResponse response) throws Exception {
        response.ok("text/html;charset=utf-8", new byte[0]);

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
