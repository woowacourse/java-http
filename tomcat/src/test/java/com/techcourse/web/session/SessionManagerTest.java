package com.techcourse.web.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import common.session.Session;
import common.session.SessionManager;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = SessionManager.getInstance();
        sessionManager.clear();
    }

    @Test
    @DisplayName("싱글톤 패턴으로 동일한 인스턴스 반환")
    void getInstance() {
        // when
        final SessionManager instance1 = SessionManager.getInstance();
        final SessionManager instance2 = SessionManager.getInstance();

        // then
        assertSoftly(softly -> {
            softly.assertThat(instance1).isSameAs(instance2);
            softly.assertThat(instance1).isSameAs(sessionManager);
        });
    }

    @Test
    @DisplayName("새로운 세션 추가")
    void addSession() {
        // given
        final Session session = new Session();
        final String sessionId = session.getId();

        // when
        sessionManager.add(session);

        // then
        final Optional<Session> foundSession = sessionManager.find(sessionId);
        assertSoftly(softly -> {
            softly.assertThat(foundSession).isPresent();
            foundSession.ifPresent(s -> {
                softly.assertThat(s).isSameAs(session);
                softly.assertThat(s.getId()).isEqualTo(sessionId);
            });
        });
    }

    @Test
    @DisplayName("여러 세션 추가 및 조회")
    void addMultipleSessions() {
        // given
        final Session session1 = new Session();
        final Session session2 = new Session();
        final Session session3 = new Session();

        // when
        sessionManager.add(session1);
        sessionManager.add(session2);
        sessionManager.add(session3);

        // then
        assertSoftly(softly -> {
            softly.assertThat(sessionManager.find(session1.getId())).isPresent();
            softly.assertThat(sessionManager.find(session2.getId())).isPresent();
            softly.assertThat(sessionManager.find(session3.getId())).isPresent();

            sessionManager.find(session1.getId()).ifPresent(s -> softly.assertThat(s).isSameAs(session1));
            sessionManager.find(session2.getId()).ifPresent(s -> softly.assertThat(s).isSameAs(session2));
            sessionManager.find(session3.getId()).ifPresent(s -> softly.assertThat(s).isSameAs(session3));
        });
    }

    @Test
    @DisplayName("존재하지 않는 세션 ID로 조회")
    void findNonExistentSession() {
        // given
        final String nonExistentSessionId = "non-existent-session-id";

        // when
        final Optional<Session> foundSession = sessionManager.find(nonExistentSessionId);

        // then
        assertThat(foundSession).isEmpty();
    }

    @Test
    @DisplayName("세션 제거")
    void removeSession() {
        // given
        final Session session = new Session();
        sessionManager.add(session);

        // 세션이 추가되었는지 확인
        assertThat(sessionManager.find(session.getId())).isPresent();

        // when
        sessionManager.remove(session);

        // then
        final Optional<Session> foundSession = sessionManager.find(session.getId());
        assertThat(foundSession).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 세션 제거 시 예외 없이 처리")
    void removeNonExistentSession() {
        // given
        final Session session = new Session();
        // 세션을 추가하지 않음

        // when & then
        sessionManager.remove(session);

        // 여전히 존재하지 않음을 확인
        final Optional<Session> foundSession = sessionManager.find(session.getId());
        assertThat(foundSession).isEmpty();
    }

    @Test
    @DisplayName("유효한 세션 ID 검증")
    void isValidSession() {
        // given
        final Session session = new Session();
        sessionManager.add(session);

        // when & then
        assertThat(sessionManager.isValidSession(session.getId())).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 세션 ID는 유효하지 않음")
    void isInvalidSessionForNonExistentId() {
        // given
        final String nonExistentSessionId = "non-existent-session-id";

        // when & then
        assertThat(sessionManager.isValidSession(nonExistentSessionId)).isFalse();
    }

    @Test
    @DisplayName("null 세션 ID는 유효하지 않음")
    void isInvalidSessionForNullId() {
        // when & then
        assertThat(sessionManager.isValidSession(null)).isFalse();
    }

    @Test
    @DisplayName("빈 문자열 세션 ID는 유효하지 않음")
    void isInvalidSessionForEmptyId() {
        // when & then
        assertThat(sessionManager.isValidSession("")).isFalse();
        assertThat(sessionManager.isValidSession("   ")).isFalse();
    }

    @Test
    @DisplayName("세션 추가 후 제거한 세션 ID는 유효하지 않음")
    void isInvalidSessionAfterRemoval() {
        // given
        final Session session = new Session();
        sessionManager.add(session);
        final String sessionId = session.getId();

        // 세션이 유효한지 확인
        assertThat(sessionManager.isValidSession(sessionId)).isTrue();

        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.isValidSession(sessionId)).isFalse();
    }

    @Test
    @DisplayName("세션 생성 시 고유한 ID 생성")
    void sessionCreationGeneratesUniqueIds() {
        // given & when
        final Session session1 = new Session();
        final Session session2 = new Session();
        final Session session3 = new Session();

        sessionManager.add(session1);
        sessionManager.add(session2);
        sessionManager.add(session3);

        // then
        assertSoftly(softly -> {
            softly.assertThat(session1.getId()).isNotNull();
            softly.assertThat(session2.getId()).isNotNull();
            softly.assertThat(session3.getId()).isNotNull();

            softly.assertThat(session1.getId()).isNotEqualTo(session2.getId());
            softly.assertThat(session2.getId()).isNotEqualTo(session3.getId());
            softly.assertThat(session1.getId()).isNotEqualTo(session3.getId());
        });
    }
}
