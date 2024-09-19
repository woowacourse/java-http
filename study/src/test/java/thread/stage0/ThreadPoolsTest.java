package thread.stage0;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 스레드 풀은 무엇이고 어떻게 동작할까? 테스트를 통과시키고 왜 해당 결과가 나왔는지 생각해보자.
 * <p>
 * Thread Pools https://docs.oracle.com/javase/tutorial/essential/concurrency/pools.html
 * <p>
 * Introduction to Thread Pools in Java https://www.baeldung.com/thread-pool-java-and-guava
 */
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

        // 쓰레드 풀 사이즈 2로 설정했음
        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        /**
         * submit이 수행시키는 execute메서드는 비동기
         *
         * 쓰레드풀의 쓰레드 수는 2개
         * 1,2번 submit으로 인해 쓰레드 2개가 다 사용되고 3번 submit은 1,2번 중 하나가 끝날 때까지 쓰레드를 1초 대기
         * 그럼 쓰레드 1개는 작업을 대기, 1개는 작업 중이므로 이 시점 queueSize는 1
         */
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue().size());

        executor.close();
    }

    @Test
    void testNewCachedThreadPool() {
        final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        /**
         * 내 맥북 쓰레드 100개인데, 101개가 왜 돼?
         * JVM 쓰레드 1개에 실제 쓰레드 1개가 매핑되는거 아닌가?
         */
        for (int i = 0; i <101; i++) {
            executor.submit(logWithSleep("hello cached thread pools"));
        }

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 101;
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
