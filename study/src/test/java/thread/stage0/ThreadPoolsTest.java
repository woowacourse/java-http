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
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 2;
        final int expectedQueueSize = 1;

        /*
        - Thread가 두 개 존재하는 Thread Pool이 실행된다.
        - executor.submit을 통해 총 세 개의 스레드를 생성했다.
        - 각각의 스레드는 logWithSleep을 수행하게 된다.
        - 하지만 logWithSleep에서는 내부적으로 Thread.sleep을 1초동안 하므로 메인 스레드는 기다리지 않고 테스트를 마치게 된다.
        - 따라서 실행중인 스레드는 2개, 스레드 풀에 있는 스레드 개수는 1개이다.
        - 이는 테스트 코드 내에서 Thread.sleep(3000);을 하면 결과가 달라짐
        - 메인 스레드가 아닌 나머지 스레드들이 작업을 모두 마치기 때문에, Queue의 사이즈는 0이 된다.
         */

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue().size());
    }

    @Test
    void testNewCachedThreadPool() throws InterruptedException {
        final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));

        final int expectedPoolSize = 3;
        final int expectedQueueSize = 0;
        /*
        - newCachedThreadPool의 경우, '스레드가 필요할 때마다' 풀에 스레드를 하나 더 채워넣는다.
        - 이렇게 했을 때의 이점은 짧은 비동기처리에 효과적이라는 것이다.
        - 또한 가능하다면 이전에 사용이 완료된 스레드를 재사용한다.
        - 스레드의 기본 keepAliveTime은 60초로 기본 생성자에 설정되어 있다.
        - 60초동안 아무 일도 하지 않는 스레드는 풀에서 제거된다.
        - https://www.baeldung.com/java-executors-cached-fixed-threadpool
         */
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
