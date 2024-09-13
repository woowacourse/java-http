package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

class SessionManagerTest {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @BeforeEach
    void setUp() {
        sessionManager.clear();
    }

    @DisplayName("여러 스레드가 동시에 세션을 추가해도 모두 정상적으로 추가된다.")
    @RepeatedTest(10)
    void add() {
        int threadCount = 10;
        int sessionCount = 10000;
        boolean result = false;

        try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
            for (int i = 0; i < threadCount; i++) {
                executorService.execute(() -> {
                    for (int j = 0; j < sessionCount; j++) {
                        sessionManager.add(new Session(UUID.randomUUID().toString()));
                    }
                });
            }
            executorService.shutdown();
            result = executorService.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean threadCompleted = result;
        assertAll(
                () -> assertThat(threadCompleted).isTrue(),
                () -> assertThat(sessionManager.getStore()).hasSize(threadCount * sessionCount)
        );
    }
}
