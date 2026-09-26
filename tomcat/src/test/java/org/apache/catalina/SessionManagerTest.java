package org.apache.catalina;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class SessionManagerTest {

    private final Manager sessionManager = SessionManager.INSTANCE;

    /**
     * SESSIONS가 static이라 테스트끼리 저장소를 공유한다.
     * 매번 새 아이디를 써서 테스트끼리 간섭하지 않게 한다.
     */
    private Session newSession() {
        return new Session(UUID.randomUUID().toString());
    }

    @Test
    @DisplayName("담아둔 세션을 아이디로 찾을 수 있다")
    void findsAddedSession() {
        final Session session = newSession();

        sessionManager.add(session);

        assertThat(sessionManager.findSession(session.getId())).isSameAs(session);
    }

    @Test
    @DisplayName("담은 적 없는 아이디로 찾으면 null이다")
    void returnsNullForUnknownId() {
        assertThat(sessionManager.findSession(UUID.randomUUID().toString())).isNull();
    }

    @Test
    @DisplayName("세션에 담긴 값까지 그대로 꺼낼 수 있다")
    void keepsSessionAttributes() {
        final Session session = newSession();
        session.setAttribute("user", "gugu");

        sessionManager.add(session);

        final Session found = sessionManager.findSession(session.getId());
        assertThat(found.getAttribute("user")).isEqualTo("gugu");
    }

    @Test
    @DisplayName("지운 세션은 더 이상 찾을 수 없다")
    void removesSession() {
        final Session session = newSession();
        sessionManager.add(session);

        sessionManager.remove(session.getId());

        assertThat(sessionManager.findSession(session.getId())).isNull();
    }

    @Test
    @DisplayName("담은 적 없는 아이디를 지워도 예외가 나지 않는다")
    void doesNotThrowWhenRemovingUnknownId() {
        assertThatCode(() -> sessionManager.remove(UUID.randomUUID().toString()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("여러 세션을 아이디별로 따로 보관한다")
    void keepsSessionsIndependently() {
        final Session first = newSession();
        final Session second = newSession();

        sessionManager.add(first);
        sessionManager.add(second);

        assertThat(sessionManager.findSession(first.getId())).isSameAs(first);
        assertThat(sessionManager.findSession(second.getId())).isSameAs(second);
    }

    @Test
    @DisplayName("한 세션을 지워도 다른 세션은 남는다")
    void removesOnlyGivenSession() {
        final Session first = newSession();
        final Session second = newSession();
        sessionManager.add(first);
        sessionManager.add(second);

        sessionManager.remove(first.getId());

        assertThat(sessionManager.findSession(first.getId())).isNull();
        assertThat(sessionManager.findSession(second.getId())).isSameAs(second);
    }

    @Test
    @DisplayName("같은 아이디로 다시 담으면 나중 세션이 남는다")
    void overwritesSessionOfSameId() {
        final String id = UUID.randomUUID().toString();
        final Session old = new Session(id);
        final Session latest = new Session(id);

        sessionManager.add(old);
        sessionManager.add(latest);

        assertThat(sessionManager.findSession(id)).isSameAs(latest);
    }
}
