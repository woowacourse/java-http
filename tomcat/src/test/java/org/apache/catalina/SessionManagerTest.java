package org.apache.catalina;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    @Test
    void concurrentCreationReturnsTheSameSession() throws Exception {
        final var id = UUID.randomUUID().toString();
        final var start = new CountDownLatch(1);
        final var ready = new CountDownLatch(8);
        final var results = new ArrayList<Future<Session>>();

        try (final var executor = Executors.newFixedThreadPool(8)) {
            for (var i = 0; i < 8; i++) {
                results.add(executor.submit(() -> {
                    ready.countDown();
                    if (!start.await(3, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Concurrent start timed out");
                    }
                    return SessionManager.create(id);
                }));
            }
            assertThat(ready.await(3, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            final var expected = results.getFirst().get(3, TimeUnit.SECONDS);
            for (final var result : results) {
                assertThat(result.get(3, TimeUnit.SECONDS)).isSameAs(expected);
            }
            assertThat(SessionManager.findSession(id)).contains(expected);
        } finally {
            start.countDown();
            SessionManager.remove(id);
        }
    }

    @Test
    void creatingExistingSessionPreservesAttributes() {
        final var session = SessionManager.create();
        try {
            session.setAttribute("user", "younggi");

            final var existing = SessionManager.create(session.getId());

            assertThat(existing).isSameAs(session);
            assertThat(existing.getAttribute("user")).isEqualTo("younggi");
        } finally {
            SessionManager.remove(session.getId());
        }
    }

    @Test
    void createFindAndRemoveSession() {
        final var session = SessionManager.create();

        assertThat(SessionManager.findSession(session.getId())).contains(session);

        SessionManager.remove(session.getId());

        assertThat(SessionManager.findSession(session.getId())).isEmpty();
    }
}
