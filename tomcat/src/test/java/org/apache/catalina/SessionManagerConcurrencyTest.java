package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerConcurrencyTest {

    @Test
    @DisplayName("""
            ConcurrentMap을 사용하여 SessionManager에 세션을 추가할 때 동시성 문제가 발생하지 않는지 확인하는 테스트
            HashMap을 사용할 경우 put 메서드가 동기화되어 있지 않아 동시성 문제가 발생할 수 있음
            """)
    void testSessionManagerConcurrency() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        IntStream.range(0, 1000)
                .forEach(count -> executorService.submit(() -> SessionManager.add(new Session())));
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        assertThat(SessionManager.size()).isEqualTo(1000);
    }
}
