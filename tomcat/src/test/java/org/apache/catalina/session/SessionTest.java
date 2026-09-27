package org.apache.catalina.session;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    @Test
    void attributesAreIsolatedBetweenSessions() {
        final var first = new Session(UUID.randomUUID().toString());
        final var second = new Session(UUID.randomUUID().toString());
        first.setAttribute("user", "gugu");
        second.setAttribute("user", "other");

        first.removeAttribute("user");

        assertThat(first.getAttribute("user")).isNull();
        assertThat(second.getAttribute("user")).isEqualTo("other");
    }

    @Test
    void invalidateRemovesSessionAndAttributes() {
        final var session = new Session(UUID.randomUUID().toString());
        final var manager = SessionManager.getInstance();
        manager.add(session);
        session.setAttribute("user", "gugu");
        assertThat(manager.findSession(session.getId())).isSameAs(session);

        session.invalidate();

        assertThat(manager.findSession(session.getId())).isNull();
        assertThat(session.getAttribute("user")).isNull();
    }
}
