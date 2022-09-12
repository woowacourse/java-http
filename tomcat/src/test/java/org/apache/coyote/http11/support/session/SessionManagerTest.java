package org.apache.coyote.http11.support.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

class SessionManagerTest {

    @BeforeEach
    void setUp() {

    }

    @DisplayName("SessionManager는 스레드 안정성 및 원자성을 보장한다.")
    @Test
    void synchronizedSessionManager() throws InterruptedException {
        // given
        var executorService = Executors.newFixedThreadPool(3);

        final SessionManager sessionManager = SessionManager.getInstance();
        final int size = sessionManager.size();

        // when
        IntStream.range(0, 1000)
                .forEach(count -> addSession(sessionManager, executorService, count));
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        // then
        assertThat(sessionManager.size()).isEqualTo(size + 1000);
    }

    private static void addSession(final SessionManager sessionManager, final ExecutorService executorService, final int value) {
        executorService.submit(() -> sessionManager.add(new Session(String.valueOf(value))));
    }
}
