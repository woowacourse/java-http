package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 세션을_여러_번_요청해도_같은_세션을_반환한다() {
        // given
        final HttpRequest request = createRequest(List.of());

        // when
        final Session first = request.getSession();
        final Session second = request.getSession();

        // then
        assertThat(second).isSameAs(first);
    }

    @Test
    void 세션을_생성하지_않고_조회할_수_있다() {
        // given
        final HttpRequest request = createRequest(List.of());

        // when
        final Session session = request.getSession(false);

        // then
        assertThat(session).isNull();
        assertThat(request.createdSession()).isEmpty();
    }

    @Test
    void 기존_세션을_재사용하면_새로_생성된_세션으로_표시하지_않는다() {
        // given
        final String sessionId = "existing-session-id";
        final Session existingSession = Session.init(sessionId);
        SessionManager.getInstance().add(existingSession);
        final HttpRequest request = createRequest(
            List.of("Cookie: JSESSIONID=" + sessionId));

        // when
        final Session session = request.getSession();

        // then
        assertThat(session).isSameAs(existingSession);
        assertThat(request.createdSession()).isEmpty();

        SessionManager.getInstance().remove(sessionId);
    }

    @Test
    void 존재하지_않는_세션을_요청하면_새_세션을_생성한다() {
        // given
        final HttpRequest request = createRequest(
            List.of("Cookie: JSESSIONID=unknown-session-id"));

        // when
        final Session session = request.getSession();

        // then
        assertThat(request.createdSession()).containsSame(session);
        assertThat(session.id()).isNotEqualTo("unknown-session-id");

        SessionManager.getInstance().remove(session.id());
    }

    private HttpRequest createRequest(final List<String> headerLines) {
        return new HttpRequest(
            new RequestLine(HttpMethod.GET, "/login", HttpVersion.VERSION_11),
            HttpHeaders.from(headerLines),
            "");
    }
}
