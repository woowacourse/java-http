package study;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExecutorServiceTest {

    @Test
    @DisplayName("excute 학습 테스트, 예외가 발생하면 해당 쓰레드를 종료시키고, 새로운 쓰레드를 생성한다.")
    void execute() {
        // given
        final ExecutorService executorService = Executors.newFixedThreadPool(2);

        // when
        for (int i = 0; i < 10; i++) {
            executorService.execute(() -> {
                System.out.println(Thread.currentThread().getName());
                throw new RuntimeException();
            });

        }
        executorService.shutdown();
    }

    @Test
    @DisplayName("submit 학습 테스트, 예외가 발생해도 쓰레드를 종료하지 않고, 재활용한다.")
    void submit() {
        // given
        final ExecutorService executorService = Executors.newFixedThreadPool(2);

        // when
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                System.out.println(Thread.currentThread().getName());
                throw new RuntimeException();
            });

        }
        executorService.shutdown();
    }
}
