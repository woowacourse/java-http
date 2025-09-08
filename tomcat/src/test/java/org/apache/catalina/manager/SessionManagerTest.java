package org.apache.catalina.manager;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.domain.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
    }

    @DisplayName("createSession은 새로운 세션을 생성하고 저장한다")
    @Test
    void createSession() {
        // when
        Session session = sessionManager.createSession();

        // then
        assertThat(session).isNotNull();
        assertThat(session.getId()).isNotNull();
        assertThat(sessionManager.findSessionById(session.getId())).isEqualTo(session);
    }

    @DisplayName("findSessionById로 저장된 세션을 조회할 수 있다")
    @Test
    void findSessionById() {
        // given
        Session session = sessionManager.createSession();
        String sessionId = session.getId();

        // when
        Session foundSession = sessionManager.findSessionById(sessionId);

        // then
        assertThat(foundSession).isEqualTo(session);
        assertThat(foundSession.getId()).isEqualTo(sessionId);
    }

    @DisplayName("존재하지 않는 세션 ID로 조회하면 null을 반환한다")
    @Test
    void findSessionById_notExists() {
        // given
        String nonExistentId = "non-existent-id";

        // when
        Session session = sessionManager.findSessionById(nonExistentId);

        // then
        assertThat(session).isNull();
    }

    @DisplayName("add 메서드로 세션을 수동으로 추가할 수 있다")
    @Test
    void add() {
        // given
        Session session = new Session("custom-session-id");

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSessionById("custom-session-id")).isEqualTo(session);
    }

    @DisplayName("remove 메서드로 세션을 제거할 수 있다")
    @Test
    void remove() {
        // given
        Session session = sessionManager.createSession();
        String sessionId = session.getId();

        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.findSessionById(sessionId)).isNull();
    }

    @DisplayName("여러 세션을 독립적으로 관리할 수 있다")
    @Test
    void multipleSessionsIndependence() {
        // given
        Session session1 = sessionManager.createSession();
        Session session2 = sessionManager.createSession();

        // when
        session1.setAttribute("user", "user1");
        session2.setAttribute("user", "user2");

        // then
        assertThat(session1.getAttribute("user")).isEqualTo("user1");
        assertThat(session2.getAttribute("user")).isEqualTo("user2");
        assertThat(session1.getId()).isNotEqualTo(session2.getId());
    }
}