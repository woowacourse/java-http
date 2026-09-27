package org.apache.catalina;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerConcurrencyTest {

    private static final int SESSION_COUNT = 1_000;

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Test
    void concurrently_adds_and_finds_sessions() throws Exception {
        final List<HttpSession> sessions = IntStream.range(0, SESSION_COUNT)
                .mapToObj(index -> new Session("concurrent-session-" + index))
                .map(HttpSession.class::cast)
                .toList();
        final ExecutorService executor = Executors.newFixedThreadPool(16);

        try {
            final List<Callable<Void>> addTasks = sessions.stream()
                    .<Callable<Void>>map(session -> () -> {
                        sessionManager.add(session);
                        return null;
                    })
                    .toList();

            executor.invokeAll(addTasks);

            for (final HttpSession session : sessions) {
                assertThat(sessionManager.findSession(session.getId())).isSameAs(session);
            }
        } finally {
            sessions.forEach(sessionManager::remove);
            executor.shutdown();
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }
    }
}
