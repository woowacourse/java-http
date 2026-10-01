package thread.stage0;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class ThreadPoolsTest {

    @Test
    void testNewFixedThreadPool() throws InterruptedException {
        final var executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        final var started = new CountDownLatch(2);
        final var release = new CountDownLatch(1);

        try {
            executor.execute(waitingTask(started, release));
            executor.execute(waitingTask(started, release));

            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

            executor.execute(() -> {
            });

            assertThat(executor.getPoolSize()).isEqualTo(2);
            assertThat(executor.getActiveCount()).isEqualTo(2);
            assertThat(executor.getQueue()).hasSize(1);
        } finally {
            release.countDown();
            executor.shutdown();

            assertThat(
                    executor.awaitTermination(5, TimeUnit.SECONDS)
            ).isTrue();
        }
    }

    @Test
    void testNewCachedThreadPool() throws InterruptedException {
        final var executor =
                (ThreadPoolExecutor) Executors.newCachedThreadPool();

        final var started = new CountDownLatch(3);
        final var release = new CountDownLatch(1);

        try {
            executor.execute(waitingTask(started, release));
            executor.execute(waitingTask(started, release));
            executor.execute(waitingTask(started, release));

            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();

            assertThat(executor.getPoolSize()).isEqualTo(3);
            assertThat(executor.getActiveCount()).isEqualTo(3);
            assertThat(executor.getQueue()).isEmpty();
        } finally {
            release.countDown();
            executor.shutdown();

            assertThat(
                    executor.awaitTermination(5, TimeUnit.SECONDS)
            ).isTrue();
        }
    }

    private Runnable waitingTask(
            final CountDownLatch started,
            final CountDownLatch release
    ) {
        return () -> {
            started.countDown();

            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(e);
            }
        };
    }
}
