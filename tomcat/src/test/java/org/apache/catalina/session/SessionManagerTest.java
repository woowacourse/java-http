package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private final Clock clock = mock(Clock.class);
    private final SessionManager manager = new SessionManager(clock);

    @Test
    void expiresAtThirtyMinutesEvenWhenAccessedBeforeTheDeadline() {
        final Session session = addSession("expiring");
        session.setAttribute("user", "moa");
        when(clock.millis()).thenReturn(Duration.ofMinutes(30).toMillis() - 1);
        assertThat(manager.findSession("expiring")).isSameAs(session);
        when(clock.millis()).thenReturn(Duration.ofMinutes(30).toMillis());
        assertThat(manager.findSession("expiring")).isNull();
        assertThatIllegalStateException().isThrownBy(() -> session.getAttribute("user"));
    }

    @Test
    void requestsWithoutSessionIdsCleanUpExpiredSessionsAndKeepRecentOnes() {
        final Session expired = addSession("expired");
        when(clock.millis()).thenReturn(Duration.ofMinutes(15).toMillis());
        final Session recent = addSession("recent");
        when(clock.millis()).thenReturn(Duration.ofMinutes(30).toMillis());

        assertThat(manager.findSession(null)).isNull();
        assertThatIllegalStateException().isThrownBy(() -> expired.getAttribute("user"));
        assertThat(manager.findSession("recent")).isSameAs(recent);
    }

    @Test
    void addingSessionsAlsoCleansUpExpiredEntries() {
        final Session expired = addSession("expired");
        when(clock.millis()).thenReturn(Duration.ofMinutes(30).toMillis());

        final Session recent = addSession("recent");

        assertThatIllegalStateException().isThrownBy(() -> expired.getAttribute("user"));
        assertThat(manager.findSession("recent")).isSameAs(recent);
    }

    @Test
    void invalidatingAnOldSessionDoesNotRemoveItsReplacement() {
        final Session original = addSession("same-id");
        final Session replacement = addSession("same-id");

        original.invalidate();

        assertThat(manager.findSession("same-id")).isSameAs(replacement);
    }

    private Session addSession(String id) {
        final Session session = new Session(id, manager);
        manager.add(session);
        return session;
    }
}
