package org.apache.coyote.web.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void createCookie() {
        Cookie cookie = SessionManager.createCookie();
        assertThat(cookie).isNotNull();
    }

    @Test
    void addSession() {
        Session session = new Session("id");
        SessionManager.addSession("asfasf", session);

        Session foundSession = SessionManager.findSession("asfasf").get();
        assertThat(foundSession.getId()).isEqualTo("id");
    }

    @Test
    void remove() {
        Session session = new Session("id");
        SessionManager.addSession("asfasf", session);

        SessionManager.remove("asfasf");
        assertThat(SessionManager.findSession("asfasf").isEmpty()).isTrue();
    }
}
