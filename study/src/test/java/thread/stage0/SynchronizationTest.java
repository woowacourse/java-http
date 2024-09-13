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
        var executorService = Executors.newFixedThreadPool(3);
        var synchronizedMethods = new SynchronizedMethods();

        // 방법 1
        IntStream.range(0, 1000)
                .forEach(count -> executorService.submit(synchronizedMethods::synchronizedCalculate));
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        // 방법 2
        IntStream.range(0, 1000)
                .forEach(count -> executorService.submit(SynchronizedMethods::syncStaticCalculate));
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        // 방법 3
        IntStream.range(0, 1000)
                .forEach(count -> executorService.submit(synchronizedMethods::performSynchronizedTask));
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        assertThat(SynchronizedMethods.staticSum).isEqualTo(1000);
    }

    private static final class SynchronizedMethods {

        private static int staticSum = 0;

        private int sum = 0;

        // 방법 1: Synchronized Instance Methods: Only one thread per instance of the class can execute this method.
        public synchronized void synchronizedCalculate() {
            setSum(getSum() + 1);
        }

        // 방법 2: Synchronized Static Methods: Only one thread can execute inside a static synchronized method.
        public static synchronized void syncStaticCalculate() {
            staticSum = staticSum + 1;
        }

        // 방법 3: Synchronized Blocks Within Methods:
        public void performSynchronizedTask() {
            synchronized (this) {
                calculate();
            }
        }

        public void calculate() {
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
