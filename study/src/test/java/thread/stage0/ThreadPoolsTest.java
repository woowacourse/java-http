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
    void testNewFixedThreadPool() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        // corePoolSize = 2
        // maximumPoolSize = 2
        // keepAliveTime = 0

        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));
        // logWithoutSleep 진행 시 queue에 쌓이지 X => 3, 0

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 2;
        final int expectedQueueSize = 1; // holding tasks 저장

        assertThat(executor.getPoolSize()).isEqualTo(expectedPoolSize);
        assertThat(executor.getQueue().size()).isEqualTo(expectedQueueSize);
    }

    @Test
    void testNewCachedThreadPool() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        // corePoolSize = 0
        // maximumPoolSize = MAX_VALUE
        // keepAliveTime = 60s

        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));
        // 실험) maximumPoolSize 이상 횟수로 실행시키면? -> OutOfMemoryError

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final long expectedPoolSize = 3;
        final long expectedQueueSize = 0;

        assertThat(executor.getPoolSize()).isEqualTo(expectedPoolSize);
        assertThat(executor.getQueue().size()).isEqualTo(expectedQueueSize);
    }

    private Runnable logWithSleep(final String message) {
        // 1초 후 로깅
        return () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info(message);
        };
    }

    private Runnable logWithoutSleep(final String message) {
        return () -> {
            log.info(message);
        };
    }
}
