package org.apache.catalina;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    @Test
    void addsFindsAndRemovesSession() {
        SessionManager manager = SessionManager.getInstance();
        Session session = new Session(UUID.randomUUID().toString());

        manager.add(session);
        assertThat(manager.findSession(session.getId())).isSameAs(session);

        manager.remove(session.getId());
        assertThat(manager.findSession(session.getId())).isNull();
    }
}
