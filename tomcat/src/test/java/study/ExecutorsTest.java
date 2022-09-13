package study;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

public class ExecutorsTest {

    @DisplayName("test")
    @RepeatedTest(10)
    void test() {
        // given
        ThreadPoolExecutor executor =
                (ThreadPoolExecutor) Executors.newFixedThreadPool(2);

        // when
        /*
        - 3개의 쓰레드를 실행시킨다.
        - 쓰레드가 실행될 때 1초간 지연시킨다.
        - 쓰레드풀의 크기는 2이므로 마지막에 실행된 쓰레드는 1초를 기다려야 한다.
         */
        for (int i = 0; i < 3; i++) {
            executor.execute(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // then
        /*
        - 1초를 기다리기 전에 assertThat이 실행되면 Queue에 대기하고 있는 쓰레드가 1개 있다.
         */
        assertThat(executor.getPoolSize()).isEqualTo(2);
        assertThat(executor.getQueue().size()).isEqualTo(1);
    }
}
