package org.apache.coyote.common;

import org.apache.coyote.request.RequestHeaders;
import org.apache.coyote.session.Cookies;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestHeadersTest {

    @Test
    void 헤더와_값이_하나의_문자열로_놓여진_상태의_목록을_이용하여_생성에_성공한다() {
        // given
        final Headers headers = Headers.empty();
        headers.addHeader("Accept", "text/html;charset=utf-8");
        headers.addHeader("Connection", "keep-alive");

        // expect
        assertThatCode(() -> RequestHeaders.from(headers))
                .doesNotThrowAnyException();
    }

    @Test
    void 헤더에_쿠키와_내부에_JSESSIONID가_있을_경우_쿠키와_세션에_값이_들어가도록_생성한다() {
        // given
        final String sessionId = UUID.randomUUID().toString();
        final Session session = new Session(sessionId);
        session.setAttribute("name", "hyena");
        SessionManager.add(session);

        final Headers headers = Headers.empty();
        headers.addHeader("Accept", "text/html;charset=utf-8");
        headers.addHeader("Connection", "keep-alive");
        headers.addHeader("Cookie", "JSESSIONID=" + sessionId);

        // when
        final RequestHeaders requestHeaders = RequestHeaders.from(headers);

        // then
        final Headers actualHeaders = requestHeaders.headers();
        final Session actualSessions = requestHeaders.session();

        assertAll(
                () -> assertThat(actualHeaders.getHeaderValue("Accept")).isEqualTo("text/html;charset=utf-8"),
                () -> assertThat(actualHeaders.getHeaderValue("Connection")).isEqualTo("keep-alive"),
                () -> assertThat(actualHeaders.getHeaderValue("Cookie")).isEqualTo("JSESSIONID=" + sessionId),
                () -> assertThat(actualSessions.getAttribute("name")).isEqualTo("hyena")
        );
    }

    @Test
    void 헤더에_쿠키가_없을_경우에_빈_쿠키를_반환한다() {
        // given
        final Headers headers = Headers.empty();
        headers.addHeader("Accept", "text/html;charset=utf-8");
        headers.addHeader("Connection", "keep-alive");

        // when
        final RequestHeaders actual = RequestHeaders.from(headers);

        // expect
        assertThat(actual.cookies()).isEqualTo(Cookies.empty());
    }

    @Test
    void 헤더에_세션이_없을_경우에_빈_세션을_반환한다() {
        // given
        final Headers headers = Headers.empty();
        headers.addHeader("Accept", "text/html;charset=utf-8");
        headers.addHeader("Connection", "keep-alive");

        // when
        final RequestHeaders actual = RequestHeaders.from(headers);

        // then
        assertThat(actual.session()).isEqualTo(Session.empty());
    }
}
