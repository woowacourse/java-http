package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {

    @DisplayName("Session의 newInstance 메서드는 새로운 세션을 생성하고, 세션 매니저에 등록한 뒤 반환한다")
    @Test
    void newInstance() {
        // given
        final Session session = Session.newInstance();
        final String sessionId = session.getId();

        // when
        final Optional<Session> foundSession = SessionManager.findSession(sessionId);
        final String foundSessionId = foundSession.get().getId();

        // then
        assertAll(
                () -> assertThat(foundSession).isPresent(),
                () -> assertThat(sessionId).isEqualTo(foundSessionId)
        );
    }

    @DisplayName("Session의 invalidate 메서드는 SessionManager에서 해당 세션을 제거한다")
    @Test
    void invalidate() {
        // given
        final Session session = Session.newInstance();
        final String sessionId = session.getId();

        // when
        final Optional<Session> beforeInvalidate = SessionManager.findSession(sessionId);
        session.invalidate();
        final Optional<Session> afterInvalidate = SessionManager.findSession(sessionId);

        // then
        assertAll(
                () -> assertThat(beforeInvalidate).isPresent(),
                () -> assertThat(beforeInvalidate.get().getId()).isEqualTo(sessionId),
                () -> assertThat(afterInvalidate).isEmpty()
        );
    }

    @DisplayName("Session의 Attribute를 set, get, remove 로 CRUD할 수 있다")
    @Test
    void attribute() {
        // given
        final Session session = Session.newInstance();
        final String attributeName = "name";
        final String nameBeforeSet = session.getAttribute(attributeName);

        // when
        session.setAttribute(attributeName, "Richard");
        final String nameAfterSet = session.getAttribute(attributeName);
        session.setAttribute(attributeName, "Updated Richard");
        final String nameAfterSetAgain = session.getAttribute(attributeName);
        session.removeAttribute(attributeName);
        final String nameAfterRemove = session.getAttribute(attributeName);

        // then
        assertAll(
                () -> assertThat(nameBeforeSet).isNull(),
                () -> assertThat(nameAfterSet).isEqualTo("Richard"),
                () -> assertThat(nameAfterSetAgain).isEqualTo("Updated Richard"),
                () -> assertThat(nameAfterRemove).isNull()
        );
    }
}
