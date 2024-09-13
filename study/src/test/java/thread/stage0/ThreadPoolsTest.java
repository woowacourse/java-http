package thread.stage0;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
class ThreadPoolsTest {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolsTest.class);

    @Test
    void testNewFixedThreadPool() {
        //스레드 수 2개인 스레드 풀 생성
        final var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        //3개의 작업 실행
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        //현재 활성된 스레드의 갯수는 2개
        final int expectedPoolSize = 2;
        //현재 대기 큐에 남아있는것은 스레풀ㅇ에 있는 스레드가 2개라서 1개
        final int expectedQueueSize = 1;

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue().size());
    }

    @Test
    void testNewCachedThreadPool() throws InterruptedException {
        final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 3;
        final int expectedQueueSize = 0;
        //Thread.sleep(1001); 메세지 보기
        //executor.shutdown(); //스레드 종료 대기 -> 새로운 작업 x 진행 중인 스레드 종료되면 바로 풀 닫힘
        //executor.submit(logWithSleep("hello cached thread pools")); // shutdown 호출 후면 새로운 작업을 안받아서 예외 발생
        //executor.close(); // 그냥 종료 , 진행 중인 스레가 있던 말던 풀 없애버림 테스트 돌리면 PoolSize 0

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue().size());
    }

    /*
    비활성 스레드 유지시간 1초로 했더니, 스레드 작업 종료 후, 스레드 풀의 (활성 + keepAliveTime 동안 대기 중)인  갯수가 0임
    keepAliveTime을 설정하면, 비활성 상태라도 설정한 시간만큼 스레드 풀에서 유지됨
     */
    @Test
    void testKeepAlive() throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                0,
                10,
                1,
                TimeUnit.SECONDS,
                new SynchronousQueue<>()
        );

        executor.submit(logWithSleep("hello cached thread pools", 0));
        executor.submit(logWithSleep("hello cached thread pools", 0));
        executor.submit(logWithSleep("hello cached thread pools", 0));
        Thread.sleep(1050);

        assertThat(executor.getPoolSize()).isEqualTo(0);
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

    private Runnable logWithSleep(final String message, final long sleepMillis) {
        return () -> {
            try {
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info(message);
        };
    }
}
