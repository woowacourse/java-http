package org.apache.coyote.httprequest;

import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class HttpRequestTest {

    @Test
    void 세션_매니저에_현재_사용자의_세션이_없고_저장을_원하면_세션_매니저에_세션을_저장한다() throws IOException {
        // given
        final String jSessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";
        final String request = "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Accept: */*\n" +
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=" + jSessionId + "\n";
        final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        final HttpRequest httpRequest = HttpRequest.from(inputStream);

        // when
        final SessionManager sessionManager = SessionManager.getInstance();
        assertThat(sessionManager.findSession(jSessionId)).isNull();

        final Session session = httpRequest.getSession(false);

        // then
        assertThat(sessionManager.findSession(jSessionId)).isEqualTo(session);
    }

    @Test
    void 세션_매니저에_현재_사용자의_세션이_있고_갱신을_원하지_않으면_기존_세션을_불러온다() throws IOException {
        // given
        final String jSessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";
        final String request = "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Accept: */*\n" +
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=" + jSessionId + "\n";
        final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        final HttpRequest httpRequest = HttpRequest.from(inputStream);

        // when
        final SessionManager sessionManager = SessionManager.getInstance();
        final Session session = new Session(jSessionId);
        sessionManager.add(session);

        final Session foundSession = httpRequest.getSession(false);

        // then
        assertThat(foundSession).isEqualTo(session);
    }

    @Test
    void 세션_매니저에_현재_사용자의_세션이_없고_생성도_원하지_않고_쿠키에_세션_id가_없으면_세션을_조회할_때_null을_조회한다() throws IOException {
        // given
        final String request = "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Accept: */*\n" +
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry\n";
        final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        final HttpRequest httpRequest = HttpRequest.from(inputStream);

        // when
        final Session foundSession = httpRequest.getSession(false);

        // then
        assertThat(foundSession).isNull();
    }

    @Test
    void 새로운_세션_발급을_원하면_발급하되_기존_세션은_지운다() throws IOException {
        // given
        final String jSessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";
        final String request = "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Accept: */*\n" +
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=" + jSessionId + "\n";
        final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        final HttpRequest httpRequest = HttpRequest.from(inputStream);

        // when
        final SessionManager sessionManager = SessionManager.getInstance();
        final Session oldSession = new Session(jSessionId);
        sessionManager.add(oldSession);

        final Session foundSession = httpRequest.getSession(true);

        // then
        assertThat(sessionManager.findSession(jSessionId)).isNull();
        assertThat(foundSession).isNotEqualTo(oldSession);
    }
}
