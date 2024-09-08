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
        // 인자로 설정한 스레드 개수만큼 스레드를 처리한다.
        final var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = executor.getPoolSize();
        final int expectedQueueSize = executor.getQueue().size();

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue().size());
    }

    /*
    newFixedThreadPool은 스레드 수가 제한되어 있어 대기열이 생길 수 있다. 즉, 고정된 수의 스레드를 유지한다.
    위 코드에서는 2개의 스레드를 생성했으므로, 동시에 2개의 작업만 처리할 수 있다.
    세 번째 작업이 제출되면, 첫 번째와 두 번째 작업이 모두 처리 중일 때 대기열에 추가되므로, 대기열의 크기는 1이 된다.

    newCachedThreadPool은 동적으로 스레드를 생성하므로 대기열이 발생하지 않아
    세 개의 작업을 제출했을 때, 각 작업이 거의 동시에 처리될 수 있기 때문에 모든 작업이 즉시 실행되어
    대기열에 남는 작업이 없으므로, 대기열의 크기는 0이 된다.
     */
    @Test
    void testNewCachedThreadPool() {
        // 스레드 작업 개수보다 실 작업 개수가 많으며면 새 스레드를 생성시켜 작업을 처리한다.
        final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = executor.getPoolSize();
        final int expectedQueueSize = 0;

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue().size());
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
