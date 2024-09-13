package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    @DisplayName("세션을 추가할 수 있다.")
    void add() {
        Session session = new Session();

        SessionManager.add(session);

        assertThat(SessionManager.findSession(session.getId())).isNotNull();
    }

    @Test
    @DisplayName("세션을 제거할 수 있어야 한다")
    void remove() {
        Session session = new Session();

        SessionManager.add(session);
        SessionManager.remove(session.getId());

        assertThat(SessionManager.findSession(session.getId())).isNull();
    }
}
