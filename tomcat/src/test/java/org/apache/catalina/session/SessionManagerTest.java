package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void Session_객체를_생성하면_id를_추가해서_생성한다() {
        // given
        SessionManager manager = SessionManager.getInstance();

        // when
        Session session = manager.createSession();

        // then
        assertThat(session.getId()).isNotNull();
    }
}
