package org.apache.coyote.http11;

import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import support.StubSocket;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

class RequestSessionTest {

    @Test
    void 기존_세션만_조회할_때_세션이_없으면_null을_반환한다() throws IOException {
        // given
        HttpRequest request = request("unknown-session-id");

        // when
        Session session = request.getSession(false);

        // then
        assertThat(session).isNull();
        assertThat(request.getNewSession()).isEmpty();
    }

    @Test
    void 세션_생성을_허용하면_기존_세션이_없을_때_새_세션을_반환한다() throws IOException {
        // given
        HttpRequest request = request("unknown-session-id");

        // when
        Session session = request.getSession(true);

        // then
        assertThat(SessionManager.find(session.getId())).containsSame(session);
        assertThat(request.getNewSession()).containsSame(session);
    }

    @Test
    void 같은_요청에서_세션을_여러_번_조회하면_동일한_세션을_반환한다() throws IOException {
        // given
        HttpRequest request = request("unknown-session-id");
        Session created = request.getSession(true);

        // when
        Session found = request.getSession(true);

        // then
        assertThat(found).isSameAs(created);
        assertThat(request.getSession(false)).isSameAs(created);
    }

    @Test
    void 기존_세션_조회에_실패한_뒤에도_같은_요청에서_세션을_생성할_수_있다() throws IOException {
        // given
        HttpRequest request = request("unknown-session-id");
        assertThat(request.getSession(false)).isNull();

        // when
        Session session = request.getSession(true);

        // then
        assertThat(request.getSession(false)).isSameAs(session);
        assertThat(SessionManager.find(session.getId())).containsSame(session);
    }

    @Test
    void 쿠키의_JSESSIONID로_기존_세션을_찾으면_생성_허용_여부와_관계없이_재사용한다() throws IOException {
        // given
        Session existing = SessionManager.create();
        HttpRequest request = request(existing.getId());

        // when
        Session found = request.getSession(false);

        // then
        assertThat(found).isSameAs(existing);
        assertThat(request.getSession(true)).isSameAs(existing);
        assertThat(request.getNewSession()).isEmpty();
    }

    @Test
    void 정적_파일_요청에서는_세션_저장소를_조회하거나_세션을_생성하지_않는다() {
        // given
        StubSocket socket = new StubSocket("GET /css/styles.css HTTP/1.1\r\nCookie: JSESSIONID=existing\r\n\r\n");
        try (MockedStatic<SessionManager> sessions = mockStatic(SessionManager.class)) {
            // when
            new Http11Processor(socket).process(socket);

            // then
            assertThat(socket.output()).startsWith("HTTP/1.1 200 OK");
            sessions.verifyNoInteractions();
        }
    }

    @Test
    void 회원가입_요청에서는_세션_저장소를_조회하거나_세션을_생성하지_않는다() {
        // given
        String body = "account=" + UUID.randomUUID() + "&password=password&email=user%40example.com";
        StubSocket socket = new StubSocket("POST /register HTTP/1.1\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n" + body);
        try (MockedStatic<SessionManager> sessions = mockStatic(SessionManager.class)) {
            // when
            new Http11Processor(socket).process(socket);

            // then
            assertThat(socket.output()).startsWith("HTTP/1.1 302 Found");
            sessions.verifyNoInteractions();
        }
    }

    private HttpRequest request(String sessionId) throws IOException {
        String raw = "GET /login HTTP/1.1\r\nCookie: theme=dark; JSESSIONID=" + sessionId + "\r\n\r\n";
        return new HttpRequestParser(new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8))).parse();
    }
}
