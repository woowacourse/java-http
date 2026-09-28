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
        var executorService = Executors.newFixedThreadPool(3);
        var synchronizedMethods = new SynchronizedMethods();

        IntStream.range(0, 1000)
                // [스레드 생성]은 [생성된 스레드의 메서드 수행]과 무관하게 독립적으로 수행된다.
                // 이 과정에서 다음과 같이 문제가 생긴다.
                // 1. 스레드 A가 getSum() 호출 -> 1을 반환 받음
                // 2. 스레드 B가 setSum(2)를 호출
                // 3. 스레드 A가 setSum(1 + 1)를 호출
                // 결과적으로 3이 되어야 할 값이 2가 된다.(덮어 씌워져서)
                .forEach(count -> executorService.submit(synchronizedMethods::calculate));
        // 각 스레드들이 처리될 시간을 대기한다.
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        assertThat(synchronizedMethods.getSum()).isEqualTo(1000);
    }

    private static final class SynchronizedMethods {

        private int sum = 0;

        // synchronized를 어디에 걸어야 할까?
        // 메서드 호출 순서는 getSum() -> getSum() + 1 -> setSum()이다.
        // setSum에만 걸면, getSum()으로 잘못된 값을 가진 스레드들이 대기하다가 순차적으로 대입하여, 값이 덮어 씌워지는 문제가 해결되지 않는다.
        // getSum에만 걸면, getSum()의 값 자체는 순차적으로 가져오지만, 그게 순차적으로 setSum된 결과임은 보장받지 못한다.
        // calculate에 걸면, 두 과정이 직렬화됨을 보장한다. 단, 개별적으로 getSum과 setSum을 호출하는 건 막지 못한다.
        // 즉, [한 번에 처리해야 하는 작업]은 [getSum으로 가져오고, 더하고, setSum]을 하는 3가지 작업 단위이다.
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
