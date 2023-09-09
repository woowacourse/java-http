package thread.stage0;

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

        log(executor);
        executor.submit(logWithSleep("hello fixed thread pools"));
        log(executor);
        executor.submit(logWithSleep("hello fixed thread pools"));
        log(executor);
        executor.submit(logWithSleep("hello fixed thread pools"));
        log(executor);

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 2;
        final int expectedQueueSize = 1;

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue().size());
        log(executor);
        Thread.sleep(1000);
        log(executor);
        Thread.sleep(1000);
        log(executor);
    }

    @Test
    void testNewCachedThreadPool() {
        final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        log(executor);
        executor.submit(logWithSleep("hello cached thread pools"));
        log(executor);
        executor.submit(logWithSleep("hello cached thread pools"));
        log(executor);
        executor.submit(logWithSleep("hello cached thread pools"));
        log(executor);

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 3;
        final int expectedQueueSize = 0;

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue().size());
        log(executor);
    }

    private static void log(final ThreadPoolExecutor executor) {
        log.info("=============> START");
        log.info("ThreadPoolExecutor.getClass() = {}", executor.getClass());
        log.info("ThreadPoolExecutor.getThreadFactory() = {}", executor.getThreadFactory().getClass());
        log.info("ThreadPoolExecutor.getQueue() = {}", executor.getQueue());
        log.info("ThreadPoolExecutor.getPoolSize() = {}", executor.getPoolSize());
        log.info("ThreadPoolExecutor.getCorePoolSize() = {}", executor.getCorePoolSize());
        log.info("ThreadPoolExecutor.getLargestPoolSize() = {}", executor.getLargestPoolSize());
        log.info("ThreadPoolExecutor.getMaximumPoolSize() = {}", executor.getMaximumPoolSize());
        log.info("ThreadPoolExecutor.getActiveCount() = {}", executor.getActiveCount());
        log.info("ThreadPoolExecutor.getTaskCount() = {}", executor.getTaskCount());
        log.info("=============> END");
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
