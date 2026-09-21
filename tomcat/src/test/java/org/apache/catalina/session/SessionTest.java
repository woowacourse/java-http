package org.apache.catalina.session;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {
    @Test
    void attributes() {
        // given
        final Session session = new Session("session-attributes");

        // when
        session.setAttribute("user", "gugu");

        // then
        assertThat(session.getId()).isEqualTo("session-attributes");
        assertThat(session.getAttribute("user")).isEqualTo("gugu");
    }

    @Test
    void removeAttribute() {
        // given
        final Session session = new Session("session-remove-attribute");
        session.setAttribute("user", "gugu");

        // when
        session.removeAttribute("user");

        // then
        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    void invalidate() {
        // given
        final Session session = new Session("session-invalidate");
        session.setAttribute("user", "gugu");

        // when
        session.invalidate();

        // then
        assertThat(session.getAttribute("user")).isNull();
    }
}
