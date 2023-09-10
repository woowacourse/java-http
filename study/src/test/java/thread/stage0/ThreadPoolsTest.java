package thread.stage0;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
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

    @Test
    void ThreadPoolExecutor의_maxPool은_queue의_작업이_전부_차면_생성된다() throws InterruptedException {
        // 최대 7개의 스레드풀, 나머지 3개의 작업은 대기
        // 하지만 예측과 다르게 큐 작업이 우선된다.
        // 큐 작업이 꽉 차면 그제서야 새로운 스레드가 생성된다.
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            2,
            7,
            0,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(3)
        );
        threadPoolExecutor.execute(logWithSleep("core job")); // core thread
        threadPoolExecutor.execute(logWithSleep("core job")); // core thread

        threadPoolExecutor.execute(logWithSleep("max pool job")); // queue
        threadPoolExecutor.execute(logWithSleep("max pool job")); // queue
        threadPoolExecutor.execute(logWithSleep("max pool job")); // queue

        threadPoolExecutor.execute(logWithSleep("queue job")); // new thread
        threadPoolExecutor.execute(logWithSleep("queue job")); // new thread
        threadPoolExecutor.execute(logWithSleep("queue job")); // new thread
        threadPoolExecutor.execute(logWithSleep("queue job")); // new thread
        threadPoolExecutor.execute(logWithSleep("queue job")); // new thread

        Thread.sleep(1000);
    }

    @Test
    void ThreadPoolExecutor의_corePool과_maxPool을_같게하고_원하는_사이즈의_큐를_지정해야_작업이_대기된다() throws InterruptedException {
        // 최대 7개의 스레드풀, 나머지 3개의 작업은 대기
        // corePool 사이즈가 초과되면 큐에 추가되므로, maxPoolSize와 같게한다.
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            7,
            7,
            0,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(3)
        );
        threadPoolExecutor.execute(logWithSleep("core job")); // new thread
        threadPoolExecutor.execute(logWithSleep("core job")); // new thread
        threadPoolExecutor.execute(logWithSleep("core job")); // new thread
        threadPoolExecutor.execute(logWithSleep("core job")); // new thread
        threadPoolExecutor.execute(logWithSleep("core job")); // new thread
        threadPoolExecutor.execute(logWithSleep("core job")); // new thread
        threadPoolExecutor.execute(logWithSleep("core job")); // new thread

        threadPoolExecutor.execute(logWithSleep("queue job")); // queue
        threadPoolExecutor.execute(logWithSleep("queue job")); // queue
        threadPoolExecutor.execute(logWithSleep("queue job")); // queue

        Thread.sleep(1000);
    }

    private Runnable logWithSleep(final String message) {
        return () -> {
            log.info(message);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
