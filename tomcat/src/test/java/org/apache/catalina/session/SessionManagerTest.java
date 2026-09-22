package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.apache.catalina.Manager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("세션 관리자")
class SessionManagerTest {

    private final Manager manager = new SessionManager();


    @BeforeEach
    void setUp() {
        manager.removeAll();
    }

    @Test
    @DisplayName("세션을 ID로 등록하고 조회한다")
    void addsAndFindsSessionById() throws IOException {
        // given
        final HttpSession session = sessionWithId("add-session-id");

        // when
        manager.add(session);

        // then
        assertThat(manager.findSession("add-session-id")).isSameAs(session);
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
        final HttpSession session = sessionWithId("remove-session-id");
        manager.add(session);

        // when
        manager.remove(session);

        // then
        assertThat(manager.findSession("remove-session-id")).isNull();
    }

    @Test
    @DisplayName("같은 ID의 다른 세션을 등록하면 거부한다")
    void rejectsDifferentSessionWithSameId() throws IOException {
        // given
        final HttpSession registeredSession = sessionWithId("duplicate-session-id");
        final HttpSession duplicateSession = sessionWithId("duplicate-session-id");
        manager.add(registeredSession);

        // when & then
        assertThatThrownBy(() -> manager.add(duplicateSession))
                .isInstanceOf(IllegalStateException.class);
        assertThat(manager.findSession("duplicate-session-id")).isSameAs(registeredSession);
    }

    @Test
    @DisplayName("같은 ID의 다른 세션으로는 등록된 세션을 제거하지 않는다")
    void doesNotRemoveSessionWhenInstanceDoesNotMatch() throws IOException {
        // given
        final HttpSession registeredSession = sessionWithId("protected-session-id");
        final HttpSession differentSession = sessionWithId("protected-session-id");
        manager.add(registeredSession);

        // when
        manager.remove(differentSession);

        // then
        assertThat(manager.findSession("protected-session-id")).isSameAs(registeredSession);
    }

    private HttpSession sessionWithId(final String id) {
        final HttpSession session = mock(HttpSession.class);
        when(session.getId()).thenReturn(id);
        return session;
    }
}
