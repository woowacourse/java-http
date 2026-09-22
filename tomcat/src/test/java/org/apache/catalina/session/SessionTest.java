package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void managesAttributes() {
        Session session = new Session("session-id");

        session.setAttribute("user", "gugu");

        assertThat(session.getId()).isEqualTo("session-id");
        assertThat(session.getAttribute("user")).isEqualTo("gugu");

        session.removeAttribute("user");

        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    void invalidatesSession() {
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = new Session("session-to-invalidate");
        session.setAttribute("user", "gugu");
        sessionManager.add(session);

        session.invalidate();

        assertThat(session.getAttribute("user")).isNull();
        assertThat(sessionManager.findSession("session-to-invalidate")).isNull();
    }
}
