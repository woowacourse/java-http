package thread.stage0;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 스레드 풀은 무엇이고 어떻게 동작할까?
 * 테스트를 통과시키고 왜 해당 결과가 나왔는지 생각해보자.
 *
 * Thread Pools
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/pools.html
 *
 * Introduction to Thread Pools in Java
 * https://www.baeldung.com/thread-pool-java-and-guava
 */
class ThreadPoolsTest {

    @Test
    void testNewFixedThreadPool() throws Exception {
        final CountDownLatch started = new CountDownLatch(2);
        final CountDownLatch release = new CountDownLatch(1);

        try (final var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2)) {
            final List<Future<?>> tasks = submitTasks(executor, started, release);

            try {
                assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(executor.getPoolSize()).isEqualTo(2);
                assertThat(executor.getQueue()).hasSize(1);
            } finally {
                release.countDown();
            }

            awaitTasks(tasks);
        }
    }

    @Test
    void testNewCachedThreadPool() throws Exception {
        final CountDownLatch started = new CountDownLatch(3);
        final CountDownLatch release = new CountDownLatch(1);

        try (final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool()) {
            final List<Future<?>> tasks = submitTasks(executor, started, release);

            try {
                assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(executor.getPoolSize()).isEqualTo(3);
                assertThat(executor.getQueue()).isEmpty();
            } finally {
                release.countDown();
            }

            awaitTasks(tasks);
        }
    }

    @Test
    void testBoundedThreadPool() throws Exception {
        final CountDownLatch started = new CountDownLatch(2);
        final CountDownLatch release = new CountDownLatch(1);

        try (final var executor = new ThreadPoolExecutor(
                2, 2,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1),
                new ThreadPoolExecutor.AbortPolicy()
        )) {
            final List<Future<?>> tasks = submitTasks(executor, started, release);

            try {
                assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
                assertThat(executor.getPoolSize()).isEqualTo(2);
                assertThat(executor.getQueue()).hasSize(1);
                assertThatThrownBy(() -> executor.execute(() -> {}))
                        .isInstanceOf(RejectedExecutionException.class);
            } finally {
                release.countDown();
            }

            awaitTasks(tasks);
        }
    }

    private List<Future<?>> submitTasks(
            final ThreadPoolExecutor executor,
            final CountDownLatch started,
            final CountDownLatch release
    ) {
        final List<Future<?>> tasks = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            tasks.add(executor.submit(() -> {
                started.countDown();
                if (!release.await(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("작업 종료 대기 시간이 초과되었습니다.");
                }
                return null;
            }));
        }

        return tasks;
    }

    private void awaitTasks(final List<Future<?>> tasks) throws Exception {
        for (final Future<?> task : tasks) {
            task.get(5, TimeUnit.SECONDS);
        }
    }
}
