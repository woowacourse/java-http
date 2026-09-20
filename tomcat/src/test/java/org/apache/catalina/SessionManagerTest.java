package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("세션 관리자")
class SessionManagerTest {

    private final Manager manager = new SessionManager();

    @Test
    @DisplayName("세션을 ID로 등록하고 조회한다")
    void addsAndFindsSessionById() throws IOException {
        // given
        final HttpSession session = sessionWithId("session-id");

        // when
        manager.add(session);

        // then
        assertThat(manager.findSession("session-id")).isSameAs(session);
    }

    @Test
    @DisplayName("등록되지 않은 ID로 조회하면 null을 반환한다")
    void returnsNullWhenSessionDoesNotExist() throws IOException {
        // when
        final HttpSession session = manager.findSession("unknown-id");

        // then
        assertThat(session).isNull();
    }

    @Test
    @DisplayName("등록된 세션을 제거한다")
    void removesSession() throws IOException {
        // given
        final HttpSession session = sessionWithId("session-id");
        manager.add(session);

        // when
        manager.remove(session);

        // then
        assertThat(manager.findSession("session-id")).isNull();
    }

    @Test
    @DisplayName("같은 ID의 세션을 다시 등록하면 최신 세션으로 교체한다")
    void replacesSessionWithSameId() throws IOException {
        // given
        final HttpSession oldSession = sessionWithId("session-id");
        final HttpSession newSession = sessionWithId("session-id");
        manager.add(oldSession);

        // when
        manager.add(newSession);

        // then
        assertThat(manager.findSession("session-id")).isSameAs(newSession);
    }

    private HttpSession sessionWithId(final String id) {
        final HttpSession session = mock(HttpSession.class);
        when(session.getId()).thenReturn(id);
        return session;
    }
}
