package org.was.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void 세션_매니저에_세션을_추가() {
        // given
        Session session = new Session();

        // when
        SessionManager.add(session);

        // then
        Session actual = SessionManager.findSession(session.getId());

        assertThat(actual.getId()).matches(session.getId());
    }
}
