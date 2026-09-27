package org.apache.catalina.session;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {
    private final SessionManager sessionManager = new SessionManager();

    @Test
    void addAndFind() {
        // given
        final Session session = new Session("manager-add");

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSession("manager-add")).isSameAs(session);
    }

    @Test
    void remove() {
        // given
        final Session session = new Session("manager-remove");
        session.setAttribute("user", "gugu");
        sessionManager.add(session);

        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.findSession("manager-remove")).isNull();
        assertThat(session.getAttribute("user")).isEqualTo("gugu");
    }

    @Test
    void removingOldSessionDoesNotRemoveReplacement() {
        // given
        final Session oldSession = new Session("same-id");
        oldSession.setAttribute("user", "old");
        final Session replacement = new Session("same-id");
        replacement.setAttribute("user", "replacement");
        sessionManager.add(oldSession);
        sessionManager.add(replacement);

        // when
        sessionManager.remove(oldSession);

        // then
        assertThat(sessionManager.findSession("same-id")).isSameAs(replacement);
        assertThat(replacement.getAttribute("user")).isEqualTo("replacement");
        assertThat(oldSession.getAttribute("user")).isEqualTo("old");
    }

    @Test
    void createSessionRegistersNewSession() {
        // when
        final Session session = sessionManager.createSession();

        // then
        assertThat(session.getId()).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
        assertThat(sessionManager.findSession(session.getId())).isSameAs(session);
    }

    @Test
    void createSessionIssuesDifferentIds() {
        // when
        final Session first = sessionManager.createSession();
        final Session second = sessionManager.createSession();

        // then
        assertThat(first.getId()).isNotEqualTo(second.getId());
    }

    @Test
    void findUnknownOrNullId() {
        assertThat(sessionManager.findSession("manager-unknown")).isNull();
        assertThat(sessionManager.findSession(null)).isNull();
    }

    @Test
    void sessionsAreNotSharedBetweenManagers() {
        // given
        final Session session = new Session("manager-not-shared");
        sessionManager.add(session);

        // when
        final SessionManager other = new SessionManager();

        // then
        assertThat(other.findSession("manager-not-shared")).isNull();
    }

    @Test
    void createsSessionsConcurrently() throws Exception {
        // given
        final int threadCount = 8;
        final int sessionsPerThread = 50;
        final var executor = Executors.newFixedThreadPool(threadCount);
        final CyclicBarrier barrier = new CyclicBarrier(threadCount);

        try {
            final List<Future<List<Session>>> futures = new ArrayList<>();
            for (int i = 0; i < threadCount; i++) {
                futures.add(executor.submit(() -> {
                    barrier.await();
                    final List<Session> created = new ArrayList<>();
                    for (int j = 0; j < sessionsPerThread; j++) {
                        created.add(sessionManager.createSession());
                    }
                    return created;
                }));
            }

            // when
            final List<Session> sessions = new ArrayList<>();
            for (final Future<List<Session>> future : futures) {
                sessions.addAll(future.get(5, TimeUnit.SECONDS));
            }

            // then
            assertThat(sessions).hasSize(threadCount * sessionsPerThread);
            assertThat(sessions).extracting(Session::getId).doesNotHaveDuplicates();
            for (final Session session : sessions) {
                assertThat(sessionManager.findSession(session.getId())).isSameAs(session);
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
