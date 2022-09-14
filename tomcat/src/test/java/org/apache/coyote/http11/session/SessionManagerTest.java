package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void isExist() {
        Session session = new Session();
        SessionManager.add(session);

        assertThat(SessionManager.isExist(session.getId())).isTrue();
    }
}
