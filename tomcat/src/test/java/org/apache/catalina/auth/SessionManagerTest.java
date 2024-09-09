package org.apache.catalina.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    @DisplayName("성공 : SessionManager 인스턴스를 얻을 수 있다.")
    void getInstance() {
        SessionManager sessionManager = SessionManager.getInstance();

        assertThat(sessionManager).isNotNull();
    }

    @Test
    @DisplayName("성공 : 세션을 추가할 수 있다.")
    void add() {
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = new Session("id");

        sessionManager.add(session);

        Optional<Session> actual = sessionManager.findSession("id");
        assertThat(actual).isEqualTo(Optional.of(session));
    }

    @Test
    @DisplayName("성공 : 해당 id의 세션이 있을 경우 해당 세션이 반환된다.")
    void findSessionSuccess() {
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = new Session("id");
        sessionManager.add(session);

        Optional<Session> actual = sessionManager.findSession("id");

        assertThat(actual).isEqualTo(Optional.of(session));
    }

    @Test
    @DisplayName("실패 : 해당 id의 세션이 없을 경우 optional 값이 반환된다.")
    void findSessionFailByNotContain() {
        SessionManager sessionManager = SessionManager.getInstance();

        Optional<Session> actual = sessionManager.findSession("id");

        assertThat(actual).isEmpty();
    }

    @Test
    @DisplayName("성공 : 세션을 제거할 수 있다.")
    void remove() {
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = new Session("id");
        sessionManager.add(session);

        sessionManager.remove(session);

        Optional<Session> actual = sessionManager.findSession("id");
        assertThat(actual).isEmpty();
    }
}
