package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    @DisplayName("동시성 테스트")
    void concurrencyTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < 100; i++) {
            String tmp = "test" + i;
            executorService.submit(() -> {
                /* for debugging
                System.out.println("Thread Name: " + Thread.currentThread().getName() +
                        "\nCurrent Time Millis: " + System.currentTimeMillis());
                */
                String sessionId = SessionManager.add(new Session(new User(tmp, tmp, tmp)));
                Session foundSession = SessionManager.findSession(sessionId);
                if (foundSession != null) {
                    successCount.incrementAndGet();
                }
            });
        }

        executorService.shutdown();
        assertThat(successCount.get()).isEqualTo(100);
    }
}
