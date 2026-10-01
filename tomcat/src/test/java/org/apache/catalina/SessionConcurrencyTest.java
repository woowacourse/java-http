package org.apache.catalina;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionConcurrencyTest {

    private static final int WORKER_COUNT = 8;
    private static final int ITERATION_COUNT = 100;

    @Test
    void createsAndFindsSessionsConcurrently() throws Exception {
        final SessionManager manager = SessionManager.getInstance();

        final var createdSessions = new ConcurrentLinkedQueue<Session>();

        try {
            runConcurrently(workerIndex -> {
                for (int i = 0; i < ITERATION_COUNT; i++) {
                    final Session session = manager.createSession();
                    createdSessions.add(session);
                }
            });

            assertThat(createdSessions).hasSize(WORKER_COUNT * ITERATION_COUNT);

            for (Session session : createdSessions) {
                assertThat(manager.findSession(session.getId())).isSameAs(session);
            }
        } finally {
            for (Session session : createdSessions) {
                manager.remove(session.getId());
            }
        }
    }

    private void runConcurrently(final IntConsumer task) throws Exception {
        final var executor = Executors.newFixedThreadPool(WORKER_COUNT);
        final var ready = new CountDownLatch(WORKER_COUNT);
        final var start = new CountDownLatch(1);
        final List<Future<?>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < WORKER_COUNT; i++) {
                final int workerIndex = i;

                futures.add(executor.submit(() -> {
                    ready.countDown();
                    start.await();

                    task.accept(workerIndex);
                    return null;
                }));
            }

            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            for (Future<?> future : futures) {
                future.get(10, TimeUnit.SECONDS);
            }
        } finally {
            start.countDown();
            executor.shutdownNow();

            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }
    }
}
