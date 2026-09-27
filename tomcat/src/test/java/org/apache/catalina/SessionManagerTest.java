package org.apache.catalina;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    private static final int SESSION_COUNT = 1_000;
    private static final int THREAD_COUNT = 16;

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Test
    void 세션_저장소로_동시성_컬렉션을_사용한다() throws Exception {
        final Field sessions = SessionManager.class.getDeclaredField("SESSIONS");
        sessions.setAccessible(true);

        assertThat(sessions.get(null)).isInstanceOf(ConcurrentMap.class);
    }

    @Test
    void 여러_스레드가_동시에_세션을_추가하고_조회하고_삭제할_수_있다() throws Exception {
        final String idPrefix = UUID.randomUUID().toString();
        final List<Session> sessions = IntStream.range(0, SESSION_COUNT)
                .mapToObj(index -> new Session(idPrefix + "-" + index))
                .toList();

        try {
            executeConcurrently(sessions.stream()
                    .map(session -> (Runnable) () -> sessionManager.add(session))
                    .toList());

            executeConcurrently(sessions.stream()
                    .map(session -> (Runnable) () ->
                            assertThat(sessionManager.findSession(session.getId())).isSameAs(session))
                    .toList());

            executeConcurrently(sessions.stream()
                    .map(session -> (Runnable) () -> sessionManager.remove(session))
                    .toList());

            assertThat(sessions)
                    .noneMatch(session -> sessionManager.isExistSession(session.getId()));
        } finally {
            sessions.forEach(sessionManager::remove);
        }
    }

    private void executeConcurrently(final List<Runnable> tasks) throws Exception {
        final var executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        final var start = new CountDownLatch(1);

        try {
            final var futures = tasks.stream()
                    .map(task -> executorService.submit(() -> {
                        start.await();
                        task.run();
                        return null;
                    }))
                    .toList();

            start.countDown();

            for (final Future<?> future : futures) {
                future.get(3, TimeUnit.SECONDS);
            }
        } finally {
            executorService.shutdownNow();
        }
    }
}
