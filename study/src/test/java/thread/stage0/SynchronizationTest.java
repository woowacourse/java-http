package thread.stage0;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 다중 스레드 환경에서 두 개 이상의 스레드가 변경 가능한(mutable) 공유 데이터를 동시에 업데이트하면 경쟁 조건(race condition)이 발생한다.
 * 자바는 공유 데이터에 대한 스레드 접근을 동기화(synchronization)하여 경쟁 조건을 방지한다.
 * 동기화된 블록은 하나의 스레드만 접근하여 실행할 수 있다.
 *
 * Synchronization
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html
 */
class SynchronizationTest {

    private static final Logger log = LoggerFactory.getLogger(SynchronizationTest.class);

    /**
     * 테스트가 성공하도록 SynchronizedMethods 클래스에 동기화를 적용해보자.
     * synchronized 키워드에 대하여 찾아보고 적용하면 된다.
     *
     * Guide to the Synchronized Keyword in Java
     * https://www.baeldung.com/java-synchronized
     */
    @Test
    void testSynchronized() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        SynchronizedMethods synchronizedMethods = new SynchronizedMethods();

        IntStream.range(0, 1000)
                .forEach(i -> executorService.submit(synchronizedMethods::calculate)); // 실행 (비동기로 요청 시도)
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS); // 정상적인 종료, 혹은 타임아웃 발생 여부 확인 후 대기

        assertThat(synchronizedMethods.getSum()).isEqualTo(1000);
    }

    private static final class SynchronizedMethods {

        private int sum = 0;

        public synchronized void calculate() {
            log.info("before calculate: {}", sum);
            setSum(getSum() + 1);
            log.info("after calculate: {}", sum);
        }

        public /*synchronized*/ int getSum() {
            return sum;
        }

        public /*synchronized*/ void setSum(int sum) {
            this.sum = sum;
        }
    }
}
