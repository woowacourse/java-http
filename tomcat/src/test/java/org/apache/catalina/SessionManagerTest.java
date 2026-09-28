package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;
import static support.ConcurrentTestSupport.runConcurrently;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

public class SessionManagerTest {

    @Test
    void session_add_success() {
        // given
        SessionManager sessionManager = SessionManager.getInstance();
        final Session session = new Session("session-id");

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSession("session-id"))
                .isEqualTo(session);
    }

    @Test
    void session_find_success() {
        // given
        SessionManager sessionManager = SessionManager.getInstance();
        final Session session = new Session("session-id");
        sessionManager.add(session);

        // when
        final Session foundSession =
                sessionManager.findSession("session-id");

        // then
        assertThat(foundSession)
                .isEqualTo(session);
    }

    @Test
    void session_remove_success() {
        // given
        final SessionManager sessionManager = SessionManager.getInstance();
        final Session session = new Session("session-id");
        sessionManager.add(session);

        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.findSession("session-id"))
                .isNull();
    }

    @Test
    void session_add_and_find_success_concurrently() throws Exception {
        // given
        final SessionManager sessionManager = SessionManager.getInstance();
        final List<Session> sessions = createSessions(1000);
        final int workerCount = 8;

        try {
            // when
            runConcurrently(workerCount, worker -> {
                for (int i = worker; i < sessions.size(); i += workerCount) {
                    final Session session = sessions.get(i);
                    sessionManager.add(session);
                    assertThat(sessionManager.findSession(session.getId()))
                            .isSameAs(session);
                }
            });

            // then
            assertThat(sessions).allSatisfy(session ->
                    assertThat(sessionManager.findSession(session.getId()))
                            .isSameAs(session));
        } finally {
            sessions.forEach(sessionManager::remove);
        }
    }

    @Test
    void session_remove_success_concurrently() throws Exception {
        // given
        final SessionManager sessionManager = SessionManager.getInstance();
        final List<Session> sessions = createSessions(1000);
        final int workerCount = 8;
        sessions.forEach(sessionManager::add);

        try {
            // when
            runConcurrently(workerCount, worker -> {
                for (int i = worker; i < sessions.size(); i += workerCount) {
                    final Session session = sessions.get(i);
                    sessionManager.remove(session);
                    assertThat(sessionManager.findSession(session.getId())).isNull();
                }
            });

            // then
            assertThat(sessions).allSatisfy(session ->
                    assertThat(sessionManager.findSession(session.getId())).isNull());
        } finally {
            sessions.forEach(sessionManager::remove);
        }
    }

    private List<Session> createSessions(final int count) {
        final String prefix = UUID.randomUUID().toString();

        return IntStream.range(0, count)
                .mapToObj(index -> new Session(prefix + "-" + index))
                .toList();
    }

}
