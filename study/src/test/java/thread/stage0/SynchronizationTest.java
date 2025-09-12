package thread.stage0;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

/**
 * 다중 스레드 환경에서 두 개 이상의 스레드가 변경 가능한(mutable) 공유 데이터를 동시에 업데이트하면 경쟁 조건(race condition)이 발생한다. 자바는 공유 데이터에 대한 스레드 접근을
 * 동기화(synchronization)하여 경쟁 조건을 방지한다. 동기화된 블록은 하나의 스레드만 접근하여 실행할 수 있다.
 * <p>
 * Synchronization https://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html
 */
class SynchronizationTest {

    /**
     * 테스트가 성공하도록 SynchronizedMethods 클래스에 동기화를 적용해보자. synchronized 키워드에 대하여 찾아보고 적용하면 된다.
     * <p>
     * Guide to the Synchronized Keyword in Java https://www.baeldung.com/java-synchronized
     */
    @Test
    void testSynchronized() throws InterruptedException {
        // 스레드 3개를 가지고 있는 스레드풀 생성
        var executorService = Executors.newFixedThreadPool(3);
        // 동기화가 필요한 메서드 예시인 SynchronizedMethods 생성
        var synchronizedMethods = new SynchronizedMethods();

        // SynchronizedMethods의 sum값을 1씩 늘리는 calculate 메서드를 1000번 실행하는 작업을 스레드풀에 등록
        // 스레드풀은 해당 작업을 3개의 스레드가 나누어 동시에 처리
        IntStream.range(0, 1000)
                .forEach(count -> executorService.submit(synchronizedMethods::calculate));

        // 스레드풀에 등록한 작업이 종료될 때까지 대기
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        // SynchronizedMethods의 sum값이 정상적으로 1000까지 늘어났는지 확인
        assertThat(synchronizedMethods.getSum()).isEqualTo(1000);
    }

    private static final class SynchronizedMethods {

        private int sum = 0;

        public synchronized void calculate() {
            setSum(getSum() + 1);
        }

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }
    }
}
