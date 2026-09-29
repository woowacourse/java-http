package org.apache.catalina.session;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    @Test
    void preservesSessionsRegisteredConcurrently() throws Exception {
        final var manager = SessionManager.getInstance();
        final var prefix = UUID.randomUUID().toString();
        final var sessions = IntStream.range(0, 1000)
                .mapToObj(index -> new Session(prefix + "-" + index))
                .toList();
        final int workerCount = 8;
        final var ready = new CountDownLatch(workerCount);
        final var start = new CountDownLatch(1);
        final var executor = Executors.newFixedThreadPool(workerCount);
        final var futures = new ArrayList<Future<?>>();

        try {
            for (int worker = 0; worker < workerCount; worker++) {
                final int offset = worker;
                futures.add(executor.submit(() -> {
                    ready.countDown();
                    if (!start.await(5, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("세션 등록 시작 대기 시간 초과");
                    }
                    for (int index = offset; index < sessions.size(); index += workerCount) {
                        manager.add(sessions.get(index));
                    }
                    return null;
                }));
            }
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();
            for (final var future : futures) {
                future.get(5, TimeUnit.SECONDS);
            }

            for (final var session : sessions) {
                assertThat(manager.findSession(session.getId())).isSameAs(session);
            }
            for (final var session : sessions) {
                manager.remove(session.getId());
                assertThat(manager.findSession(session.getId())).isNull();
            }
        } finally {
            start.countDown();
            executor.shutdownNow();
            try {
                assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
            } finally {
                sessions.forEach(session -> manager.remove(session.getId()));
            }
        }
    }
}
