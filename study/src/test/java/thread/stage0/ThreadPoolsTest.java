package thread.stage0;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
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
@DisplayName("스레드 풀")
class ThreadPoolsTest {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolsTest.class);

    @Nested
    @DisplayName("Executors로 생성하면")
    class ExecutorsTest {

        @Test
        @DisplayName("고정 스레드 풀은 코어 스레드가 모두 바쁘면 작업을 큐에 저장한다")
        void queuesTaskWhenAllFixedThreadsAreBusy() {
            final var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
            try {
                executor.submit(logWithSleep("hello fixed thread pools"));
                executor.submit(logWithSleep("hello fixed thread pools"));
                executor.submit(logWithSleep("hello fixed thread pools"));

                assertThat(executor.getPoolSize()).isEqualTo(2);
                assertThat(executor.getQueue()).hasSize(1);
            } finally {
                executor.shutdownNow();
            }
        }

        @Test
        @DisplayName("캐시 스레드 풀은 대기 큐에 작업을 저장하지 않고 스레드를 늘린다")
        void increasesThreadsWithoutQueuingTask() {
            final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
            try {
                executor.submit(logWithSleep("hello cached thread pools"));
                executor.submit(logWithSleep("hello cached thread pools"));
                executor.submit(logWithSleep("hello cached thread pools"));

                assertThat(executor.getPoolSize()).isEqualTo(3);
                assertThat(executor.getQueue()).isEmpty();
            } finally {
                executor.shutdownNow();
            }
        }
    }

    @Nested
    @DisplayName("대기열 크기를 제한하면")
    class BoundedThreadPoolTest {

        private static final int CORE_POOL_SIZE = 2;
        private static final int MAXIMUM_POOL_SIZE = 4;
        private static final int QUEUE_CAPACITY = 2;

        private CountDownLatch releaseWorkers;
        private ThreadPoolExecutor executor;
        private Runnable waitingTask;

        @BeforeEach
        void setUp() {
            releaseWorkers = new CountDownLatch(1);
            executor = new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    MAXIMUM_POOL_SIZE,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(QUEUE_CAPACITY)
            );
            waitingTask = awaitRelease(releaseWorkers);
        }

        @AfterEach
        void tearDown() {
            releaseWorkers.countDown();
            executor.shutdownNow();
        }

        @Test
        @DisplayName("코어 스레드가 모두 바쁘면 작업을 큐에 저장한다")
        void queuesTaskWhenCoreThreadsAreBusy() {
            occupyCoreThreads();

            executor.execute(waitingTask);

            assertThat(executor.getQueue()).hasSize(1);
        }

        @Test
        @DisplayName("큐까지 가득 차면 최대 크기까지 스레드를 늘린다")
        void increasesThreadsWhenQueueIsFull() {
            occupyCoreThreads();
            fillQueue();

            executor.execute(waitingTask);

            assertThat(executor.getPoolSize()).isEqualTo(CORE_POOL_SIZE + 1);
        }

        @Test
        @DisplayName("최대 스레드와 큐가 모두 가득 차면 작업을 거절한다")
        void rejectsTaskWhenThreadsAndQueueAreFull() {
            occupyCoreThreads();
            fillQueue();
            growToMaximumPoolSize();

            assertThatThrownBy(() -> executor.execute(waitingTask))
                    .isInstanceOf(RejectedExecutionException.class);
        }

        private void occupyCoreThreads() {
            repeat(CORE_POOL_SIZE, () -> executor.execute(waitingTask));
        }

        private void fillQueue() {
            repeat(QUEUE_CAPACITY, () -> executor.execute(waitingTask));
        }

        private void growToMaximumPoolSize() {
            repeat(MAXIMUM_POOL_SIZE - CORE_POOL_SIZE, () -> executor.execute(waitingTask));
        }
    }

    private void repeat(final int count, final Runnable task) {
        for (int index = 0; index < count; index++) {
            task.run();
        }
    }

    private Runnable awaitRelease(final CountDownLatch releaseWorkers) {
        return () -> {
            try {
                releaseWorkers.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    private Runnable logWithSleep(final String message) {
        return () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info(message);
        };
    }
}
