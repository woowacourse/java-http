package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    @Test
    void concurrentRequestsForTheSameIdReceiveTheSameSession() throws Exception {
        final SessionManager manager = new SessionManager();
        final int requestCount = 16;
        final CountDownLatch ready = new CountDownLatch(requestCount);
        final CountDownLatch start = new CountDownLatch(1);
        final var executor = Executors.newFixedThreadPool(requestCount);
        final var results = new ArrayList<Future<Session>>();

        try {
            for (int i = 0; i < requestCount; i++) {
                results.add(executor.submit(() -> {
                    ready.countDown();
                    if (!start.await(5, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("Concurrent requests did not start");
                    }
                    return manager.getOrCreate("same-id");
                }));
            }
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            final Session session = results.getFirst().get(5, TimeUnit.SECONDS);
            for (final Future<Session> result : results) {
                assertThat(result.get(5, TimeUnit.SECONDS)).isSameAs(session);
            }
            assertThat(manager.findSession("same-id")).isSameAs(session);
        } finally {
            start.countDown();
            executor.shutdownNow();
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Test
    void removingAnOldSessionDoesNotRemoveItsReplacement() {
        final SessionManager manager = new SessionManager();
        final Session oldSession = manager.getOrCreate("same-id");
        final Session replacement = new Session("same-id");
        manager.add(replacement);

        manager.remove(oldSession);

        assertThat(manager.findSession("same-id")).isSameAs(replacement);
        manager.remove(replacement);
        assertThat(manager.findSession("same-id")).isNull();
    }
}
