package org.apache.catalina;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    @Test
    void 세션을_추가할_수_있다() {
        // given
        final String sessionId = "sessionId";
        final Session session = new Session(sessionId);
        final SessionManager sessionManager = SessionManager.get();

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSession(sessionId)).isEqualTo(session);
    }

    @Test
    void 세션을_삭제할_수_있다() {
        // given
        final String sessionId = "sessionId";
        final Session session = new Session(sessionId);
        final SessionManager sessionManager = SessionManager.get();
        sessionManager.add(session);

        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.findSession(sessionId)).isNull();
    }

    @Test
    void 자원_접근에_대해_동시성을_제공한다() throws InterruptedException {
        // given
        final SessionManager sessionManager = SessionManager.get();
        final ExecutorService executorService = Executors.newFixedThreadPool(3);
        final int expected = 1000;

        // when
        IntStream.range(0, expected)
                .forEach(count -> executeThreads(sessionManager, executorService, count));
        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        // then
        assertThat(sessionManager.size()).isEqualTo(expected);
    }

    private Future<?> executeThreads(final SessionManager sessionManager, final ExecutorService executorService, final int count) {
        return executorService.submit(() -> sessionManager.add(new Session("key" + count)));
    }
}
