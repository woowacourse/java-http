package org.apache.coyote.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void 세션_아이디를_반환한다() {
        // given
        final var session = new Session("session-id");

        // when & then
        assertThat(session.getId()).isEqualTo("session-id");
    }

    @Test
    void 세션에_속성을_저장한다() {
        // given
        final var session = new Session("session-with-attribute");

        // when
        session.setAttribute("name", "gugu");

        // then
        assertThat(session.getAttribute("name")).isEqualTo("gugu");
    }

    @Test
    void 세션의_속성을_삭제한다() {
        // given
        final var session = new Session("session-remove-attribute");
        session.setAttribute("name", "gugu");

        // when
        session.removeAttribute("name");

        // then
        assertThat(session.getAttribute("name")).isNull();
    }

    @Test
    void 세션을_무효화하면_속성과_세션매니저의_세션을_삭제한다() {
        // given
        final var session = new Session("session-invalidate");
        session.setAttribute("name", "gugu");
        SessionManager.add(session);

        // when
        session.invalidate();

        // then
        assertThat(session.getAttribute("name")).isNull();
        assertThat(SessionManager.findSession("session-invalidate")).isNull();
    }
}
