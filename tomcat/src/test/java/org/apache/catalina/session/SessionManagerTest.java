package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    /*
        ConcurrentHashMap을 사용하면 테스트가 항상 통과하지만,
        HashMap을 사용하면 테스트가 실패할 때가 있다.
     */
    @Test
    void 스레드_세이프한지_테스트한다() throws InterruptedException {
        // given
        final var executorService = Executors.newFixedThreadPool(3);
        final var sessionManager = new SessionManager();

        // when
        IntStream.range(0, 1000)
                .forEach(count -> executorService.submit(() -> sessionManager.add(new Session())));
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);
        
        // then
        assertThat(sessionManager.getSize()).isEqualTo(1000);
    }
}
