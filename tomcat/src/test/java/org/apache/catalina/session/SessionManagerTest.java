package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SessionManagerTest {

    @Test
    @DisplayName("Session을 추가한다.")
    void addSession() {
        // given
        final SessionManager sessionManager = SessionManager.getInstance();

        // when
        final Session session = new Session("testId");
        sessionManager.add(session);
        final boolean actual = sessionManager.hasSession(session);

        // then
        assertThat(actual).isTrue();
        sessionManager.remove(session);
    }

    @Test
    @DisplayName("Session이 없는 경우 hasSession은 false를 반환한다.")
    void hasSession_false() {
        // given
        final SessionManager sessionManager = SessionManager.getInstance();
        final Session session = new Session("testId");

        // when
        final boolean actual = sessionManager.hasSession(session);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Session을 제거한다.")
    void remove() {
        // given
        final SessionManager sessionManager = SessionManager.getInstance();
        final Session session = new Session("testId");
        sessionManager.add(session);

        // when
        sessionManager.remove(session);
        final boolean actual = sessionManager.hasSession(session);

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("Session을 조회한다.")
    void findSession() {
        // given
        final SessionManager sessionManager = SessionManager.getInstance();
        final Session session = new Session("testId");
        sessionManager.add(session);

        // when
        final Session foundSession = sessionManager.findSession(session.getId());

        // then
        assertThat(foundSession.getId()).isEqualTo(session.getId());
        sessionManager.remove(session);
    }
}
