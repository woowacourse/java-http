package thread.stage0;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 스레드 풀은 무엇이고 어떻게 동작할까? 테스트를 통과시키고 왜 해당 결과가 나왔는지 생각해보자.
 * <p>
 * Thread Pools https://docs.oracle.com/javase/tutorial/essential/concurrency/pools.html
 * <p>
 * Introduction to Thread Pools in Java https://www.baeldung.com/thread-pool-java-and-guava
 */
// 단일 스레드를 생성하고 제거하는 작업이 오버헤드가 크다.
// 이를 완화시키기 위한 방법은 스레드를 미리 생성하고 관리해야 한다.
// 스레드 풀은 사전에 미리 생성한 스레드를 관리하는 공간이다.
// 스레드 풀안의 스레드의 개수를 고정시킬 수 있고(newFixedThreadPool)
// 처리 양에 따라 필요한 만큼 생성할 수 있다(newCachedThreadPool).
// FixedThreadPool에 스레드가 전부 작업을 실행하게 되면 나머지 작업들은 BlockingQueue에 저장된다.
// 작업의 개수는 queue의 사이즈다.
// 반면, newCachedThreadPool은 필요한 작업만큼 thread를 생성하여 처리하고 사용하지 않을경우 60초 뒤에 제거된다.
class ThreadPoolsTest {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolsTest.class);

    @Test
    void testNewFixedThreadPool() {
        final var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 2;
        final int expectedQueueSize = 1;

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue().size());
    }

    @Test
    void testNewCachedThreadPool() {
        final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 3;
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
