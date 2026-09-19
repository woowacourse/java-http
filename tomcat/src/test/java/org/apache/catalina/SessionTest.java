package org.apache.catalina;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    @Test
    void manageSessionAttributes() {
        final var session = new Session("session-id");

        session.setAttribute("user", "gugu");

        assertThat(session.getId()).isEqualTo("session-id");
        assertThat(session.getAttribute("user")).isEqualTo("gugu");

        session.removeAttribute("user");

        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    void invalidateRemovesAllAttributes() {
        final var session = new Session("session-id");
        session.setAttribute("user", "gugu");
        session.setAttribute("role", "member");

        session.invalidate();

        assertThat(session.getAttribute("user")).isNull();
        assertThat(session.getAttribute("role")).isNull();
    }
}
