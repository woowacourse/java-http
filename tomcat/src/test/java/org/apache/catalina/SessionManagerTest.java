package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @DisplayName("ConcurrentHashMap으로 Sessions를 관리하면 동시성 문제가 발생하지 않는다.")
    @Test
    void ConcurrentTest() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        SessionProvider sessionProvider = new SessionProvider();

        IntStream.range(0, 1000).forEach(count ->
                executorService.submit(() -> sessionProvider.add(new Session(count + "")))
        );
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        assertThat(SESSIONS.size()).isEqualTo(1000);
    }

    private static final class SessionProvider {

        public void add(Session session) {
            SESSIONS.put(session.getId(), session);
        }
    }
}
