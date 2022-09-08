package org.apache.coyote.http11.context;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void addSession() {
        // given
        SessionManager manager = new SessionManager();
        Session session = new Session("my-id");
        // when
        manager.add(session);
        // then
        assertThat(manager.findSession("my-id")).isEqualTo(session);
    }

    @Test
    void addDuplicatedSession() {
        // given
        SessionManager manager = new SessionManager();
        Session session = new Session("my-id");
        // when
        manager.add(session);
        manager.add(session);
        // then
        assertThat(manager.findSession("my-id")).isEqualTo(session);
    }
}
