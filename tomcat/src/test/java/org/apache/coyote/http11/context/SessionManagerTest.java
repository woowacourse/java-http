package org.apache.coyote.http11.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private static final SessionManager MANAGER = new SessionManager();

    @Test
    void addSession() {
        // given
        Session session = new Session("my-id");
        // when
        MANAGER.add(session);
        // then
        assertThat(MANAGER.findSession("my-id")).isEqualTo(session);
        MANAGER.remove(session);
    }

    @Test
    void addDuplicatedSession() {
        // given
        Session session = new Session("my-id");
        // when
        MANAGER.add(session);
        MANAGER.add(session);
        // then
        assertThat(MANAGER.findSession("my-id")).isEqualTo(session);
        MANAGER.remove(session);
    }
}
