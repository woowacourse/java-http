package thread.stage0;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

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
    void testWithoutSynchronized() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(3);
        var synchronizedMethods = new SynchronizedMethods();

        IntStream.range(0, 1000)
                .forEach(count -> executorService.submit(synchronizedMethods::calculate));
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        assertThat(synchronizedMethods.getSum()).isNotEqualTo(1000);
    }

    @Test
    void testWithSynchronized() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(3);
        var synchronizedMethods = new SynchronizedMethods();

        IntStream.range(0, 1000)
                .forEach(count -> executorService.submit(synchronizedMethods::calculateWithSynchronizedMethod));
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        assertThat(synchronizedMethods.getSum()).isEqualTo(1000);
    }

    @Test
    void testSynchronizedBlockInMethod() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(3);
        var synchronizedMethods = new SynchronizedMethods();

        IntStream.range(0, 1000)
                .forEach(count -> executorService.submit(synchronizedMethods::calculateWithSynchronizedBlockInMethod));
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        assertThat(synchronizedMethods.getSum()).isEqualTo(1000);
    }

    private static final class SynchronizedMethods {

        private int sum = 0;

        public void calculate() {
            setSum(getSum() + 1);
        }

        /**
         * 인스턴스 메서드는 메서드를 소유한 클래스의 인스턴스를 통해 동기화 됩니다.
         * 즉, 클래스 인스턴스당 하나의 스레드만 이 메서드를 실행할 수 있습니다.
         */
        public synchronized void calculateWithSynchronizedMethod() {
            setSum(getSum() + 1);
        }

        /**
         * 때로는 전체 메서드를 동기화하지 않고, 그 안의 일부 명령어만 동기화하고 싶을 때가 있습니다.
         * synchronized를 블록에 적용 하면 이를 달성할 수 있습니다.
         * 동기화된 블록 에 매개변수 this를 전달한 것을 주목하세요. 이것은 모니터 객체입니다.
         * 블록 내부의 코드는 모니터 객체에서 동기화됩니다.
         * 간단히 말해서, 모니터 객체당 하나의 스레드만 해당 코드 블록 내부에서 실행할 수 있습니다.
         * 메서드가 정적 이면 객체 참조 대신 클래스 이름을 전달하고 클래스는 블록 동기화를 위한 모니터가 됩니다.
         */
        public void calculateWithSynchronizedBlockInMethod() {
            synchronized (this) {
                setSum(getSum() + 1);
            }
        }

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }
    }
}
