package thread.stage0;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 스레드 풀은 무엇이고 어떻게 동작할까?
 * 테스트를 통과시키고 왜 해당 결과가 나왔는지 생각해보자.
 * <p>
 * Thread Pools
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/pools.html
 * <p>
 * Introduction to Thread Pools in Java
 * https://www.baeldung.com/thread-pool-java-and-guava
 */
class ThreadPoolsTest {

    private static final Logger log = LoggerFactory.getLogger(ThreadPoolsTest.class);

    @Test
    void testNewFixedThreadPool() throws InterruptedException {

        // 어느 시점이든 최대 개수만큼의 스레드가 활성화
        // 스레드가 다 차면 작업들은 대기한다.
        final var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));
        executor.submit(logWithSleep("hello fixed thread pools"));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 2;
        final int expectedQueueSize = 2;

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue()
                .size());

        assertThat(executor.getCompletedTaskCount()).isZero();

        executor.awaitTermination(1, TimeUnit.SECONDS);

        // getPoolSize : 스레드 풀에 존재하는 전체 스레드 수
        assertThat(executor.getActiveCount()).isEqualTo(2);

        // getActiveCount : 현재 실제 작업을 수행중인 스레드 수
        assertThat(executor.getPoolSize()).isEqualTo(2);

        //getCompleteTaskCount : 작업을 완료한 개수
        assertThat(executor.getCompletedTaskCount()).isEqualTo(2);
    }

    @Test
    void testNewCachedThreadPool() throws InterruptedException {

        // 필요할 때 새로운 스레드를 생성하나, 사용 가능한 이전 스레드가 있다면 재사용
        // 짧은 생명주기를 가지는 비동기 작업 많인 실행할때 유용
        // 60초 동안 사용하지 않은 스레드는 종료 후 캐시에서 제거

        final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));
        executor.submit(logWithSleep("hello cached thread pools"));

        IntStream.range(0,800)
                .forEach(count-> executor.submit(logWithSleep("X")));

        // 올바른 값으로 바꿔서 테스트를 통과시키자.
        final int expectedPoolSize = 803;
        final int expectedQueueSize = 0;

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue()
                .size());

        executor.awaitTermination(1,TimeUnit.SECONDS);

        assertThat(expectedPoolSize).isEqualTo(executor.getCompletedTaskCount());


        // 시간이 너무 오래 걸려서 주석처리
        // 스레드가 종료 후 제거됨을 확인
//        executor.awaitTermination(1,TimeUnit.MINUTES);
//        assertThat(executor.getPoolSize()).isZero();
//        assertThat(executor.getCompletedTaskCount()).isEqualTo(expectedPoolSize);
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
