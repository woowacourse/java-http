package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 쿠키가_없고_create가_true면_새로운_세션을_발급한다() {
        HttpRequest request = getRequest(List.of());

        Session session = request.getSession(true);

        assertThat(session).isNotNull();
        assertThat(SessionManager.findSession(session.getId())).isSameAs(session);
    }

    @Test
    void 쿠키가_없고_create가_false면_null을_반환한다() {
        HttpRequest request = getRequest(List.of());

        assertThat(request.getSession(false)).isNull();
    }

    @Test
    void 쿠키의_세션이_존재하면_해당_세션을_반환한다() {
        Session existing = new Session(UUID.randomUUID().toString());
        SessionManager.add(existing);
        HttpRequest request = getRequest(List.of("Cookie: JSESSIONID=" + existing.getId()));

        assertThat(request.getSession(false)).isSameAs(existing);
    }

    @Test
    void 쿠키의_세션이_존재하지_않고_create가_true면_새로운_세션을_발급한다() {
        String unknownId = UUID.randomUUID().toString();
        HttpRequest request = getRequest(List.of("Cookie: JSESSIONID=" + unknownId));

        Session session = request.getSession(true);

        assertThat(session).isNotNull();
        assertThat(session.getId()).isNotEqualTo(unknownId);
        assertThat(SessionManager.findSession(session.getId())).isSameAs(session);
    }

    @Test
    void 쿠키의_세션이_존재하지_않고_create가_false면_null을_반환한다() {
        HttpRequest request = getRequest(
                List.of("Cookie: JSESSIONID=" + UUID.randomUUID()));

        assertThat(request.getSession(false)).isNull();
    }

    private HttpRequest getRequest(List<String> headers) {
        return new HttpRequest(
                RequestLine.from("GET /index.html HTTP/1.1 "),
                HttpHeaders.from(headers),
                RequestBody.of(ContentType.PLAIN, ""));
    }
}
