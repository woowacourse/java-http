package org.apache.catalina.session;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    @Test
    void storesAndRemovesAttributes() {
        Session session = new Session(UUID.randomUUID().toString());

        session.setAttribute("name", "gugu");
        assertThat(session.getAttribute("name")).isEqualTo("gugu");

        session.removeAttribute("name");
        assertThat(session.getAttribute("name")).isNull();
    }

    @Test
    void invalidateRemovesSessionFromManager() {
        SessionManager manager = SessionManager.getInstance();
        Session session = new Session(UUID.randomUUID().toString());
        manager.add(session);
        session.setAttribute("name", "gugu");

        session.invalidate();

        assertThat(manager.findSession(session.getId())).isNull();
        assertThat(session.getAttribute("name")).isNull();
    }
}
