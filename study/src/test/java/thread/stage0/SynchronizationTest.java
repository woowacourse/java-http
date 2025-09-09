package thread.stage0;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 다중 스레드 환경에서 두 개 이상의 스레드가 변경 가능한(mutable) 공유 데이터를 동시에 업데이트하면 경쟁 조건(race condition)이 발생한다.
 * 자바는 공유 데이터에 대한 스레드 접근을 동기화(synchronization)하여 경쟁 조건을 방지한다.
 * 동기화된 블록은 하나의 스레드만 접근하여 실행할 수 있다.
 *
 * Synchronization
 * https://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html
 */
class SynchronizationTest {

    /**
     * 테스트가 성공하도록 SynchronizedMethods 클래스에 동기화를 적용해보자.
     * synchronized 키워드에 대하여 찾아보고 적용하면 된다.
     *
     * Guide to the Synchronized Keyword in Java
     * https://www.baeldung.com/java-synchronized
     */
    @Test
    void testSynchronized() throws InterruptedException {
        // 3개의 스레드를 가지는 고정 스레드 풀 생성
        var executorService = Executors.newFixedThreadPool(3);

        // 동기화된 계산 메서드를 가진 인스턴스 생성
        var synchronizedMethods = new SynchronizedMethods();

        // 0부터 999까지 1000번 반복하며 스레드 풀에 작업 제출
        IntStream.range(0, 1000)
                .forEach(count ->
                        // 각 작업은 synchronizedMethods의 calculate 메서드 실행 -> 3개 스레드에서 병렬로 실행
                        executorService.submit(synchronizedMethods::calculate)
                );

        // 새로운 작업 제출을 막고 기존 작업 종료 대기 시작
        executorService.shutdown();
        // 최대 500밀리초까지 작업 종료 대기 (실제 작업 모두 완료되지 않을 수 있음)
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        // 계산 결과가 1000이 맞는지 검증 (calculate가 동기화되어야 올바른 결과)
        // 만약 SynchronizedMethods.calculate() 메서드가 실제로 synchronized 키워드로 보호되지 않았다면 여러 스레드가 공유 상태를 동기화 없이 변경하여 결과값이 틀어질 것이다.
        assertThat(synchronizedMethods.getSum()).isEqualTo(1000);
    }

    /*
    synchronized
    한 번에 하나의 스레드만이 특정 코드 블록이나 메서드에 접근할 수 있도록 모니터 락(Monitor Lock) 을 이용하여 데이터의 일관성과 무결성을 유지
     */
    private static final class SynchronizedMethods {

        private int sum = 0;

        public synchronized void calculate() {
            setSum(getSum() + 1);
        } // 한번에 하나의 스레드만 접근!!

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }
    }
}
