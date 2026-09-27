package thread.stage0;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;

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

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolsTest.class);

    @Test
    void testNewFixedThreadPool() throws InterruptedException {
        final var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 2;
        final int expectedQueueSize = 1;

        CountDownLatch started = new CountDownLatch(2);
        CountDownLatch release = new CountDownLatch(1);

        try {
            executor.submit(logWithLatch("hello fixed thread pools", started, release));
            executor.submit(logWithLatch("hello fixed thread pools", started, release));
            executor.submit(logWithLatch("hello fixed thread pools", started, release));

            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(executor.getPoolSize()).isEqualTo(expectedPoolSize);
            assertThat(executor.getQueue().size()).isEqualTo(expectedQueueSize);
        } finally {
            release.countDown();
            executor.shutdownNow();
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }
    }

    @Test
    void testNewCachedThreadPool() throws InterruptedException {
        final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 3;
        final int expectedQueueSize = 0;

        CountDownLatch started = new CountDownLatch(3);
        CountDownLatch release = new CountDownLatch(1);

        try {
            executor.submit(logWithLatch("hello cached thread pools", started, release));
            executor.submit(logWithLatch("hello cached thread pools", started, release));
            executor.submit(logWithLatch("hello cached thread pools", started, release));

            assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
            assertThat(executor.getPoolSize()).isEqualTo(expectedPoolSize);
            assertThat(executor.getQueue().size()).isEqualTo(expectedQueueSize);
        } finally {
            release.countDown();
            executor.shutdownNow();
            assertThat(executor.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
        }
    }

    private Runnable logWithLatch(final String message, CountDownLatch started, CountDownLatch release) {
        return () -> {
            started.countDown();
            try {
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            log.info(message);
        };
    }
}
