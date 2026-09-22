package org.apache.catalina.session;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("세션 매니저")
class SessionManagerTest {
    private static final long THIRTY_MINUTES = Duration.ofMinutes(30).toMillis();
    private static final long TWENTY_MINUTES = Duration.ofMinutes(20).toMillis();

    private AtomicLong now;
    private SessionManager manager;

    @BeforeEach
    void setUp() {
        now = new AtomicLong(0);
        manager = new SessionManager(now::get);
    }

    @Test
    @DisplayName("새 세션을 만들어 등록한다")
    void createAndRegisterSession() {
        // when
        final HttpSession session = manager.createSession();

        // then
        assertThat(manager.findSession(session.getId())).isSameAs(session);
    }

    @Test
    @DisplayName("현재 시각을 생성 시각으로 기록한다")
    void recordCreationTime() {
        // given
        now.set(1_000L);

        // when
        final HttpSession session = manager.createSession();

        // then
        assertThat(session.getCreationTime()).isEqualTo(1_000L);
    }

    @Test
    @DisplayName("알 수 없는 아이디로 조회하면 null을 반환한다")
    void nullWhenIdIsUnknown() {
        // expect
        assertThat(manager.findSession("unknown")).isNull();
    }

    @Test
    @DisplayName("비활성 유지 시간이 지나기 전에는 세션을 찾는다")
    void findSessionBeforeExpiry() {
        // given
        final HttpSession session = manager.createSession();

        // when
        now.addAndGet(THIRTY_MINUTES - 1);

        // then
        assertThat(manager.findSession(session.getId())).isSameAs(session);
    }

    @Test
    @DisplayName("비활성 유지 시간이 지나면 세션을 찾지 못한다")
    void nullAfterExpiry() {
        // given
        final HttpSession session = manager.createSession();

        // when
        now.addAndGet(THIRTY_MINUTES);

        // then
        assertThat(manager.findSession(session.getId())).isNull();
    }

    @Test
    @DisplayName("조회할 때마다 만료 시점이 늦춰진다")
    void extendExpiryOnEveryLookup() {
        // given
        final HttpSession session = manager.createSession();

        // when
        now.addAndGet(TWENTY_MINUTES);
        manager.findSession(session.getId());
        now.addAndGet(TWENTY_MINUTES);

        // then
        assertThat(manager.findSession(session.getId())).isSameAs(session);
        assertThat(session.getLastAccessedTime()).isEqualTo(TWENTY_MINUTES * 2);
    }

    @Test
    @DisplayName("만료된 세션은 조회할 때 저장소에서 제거한다")
    void removeExpiredSessionOnLookup() {
        // given
        final HttpSession session = manager.createSession();
        now.addAndGet(THIRTY_MINUTES);
        manager.findSession(session.getId());

        // when
        session.setMaxInactiveInterval(0);

        // then
        assertThat(manager.findSession(session.getId())).isNull();
    }

    @Test
    @DisplayName("세션을 제거한다")
    void removeSession() {
        // given
        final HttpSession session = manager.createSession();

        // when
        manager.remove(session);

        // then
        assertThat(manager.findSession(session.getId())).isNull();
    }
}
