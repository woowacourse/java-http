package org.was.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void 세션_매니저에_세션을_추가() {
        // given
        Session session = new Session();

        // when
        SessionManager.add(session);

        // then
        Session actual = SessionManager.findSession(session.getId());

        assertThat(actual.getId()).matches(session.getId());
    }

    @Test
    void 동시에_여러_세션을_추가() throws InterruptedException {
        // given
        int tryCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        AtomicInteger addCount = new AtomicInteger(0);

        // when
        IntStream.range(0, tryCount)
                .forEach(count -> executorService.submit(() -> {
                            Session session = new Session();
                            SessionManager.add(session);
                            Session savedSession = SessionManager.findSession(session.getId());
                            if (savedSession != null) {
                                addCount.incrementAndGet();
                            }
                        })
                );

        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        // then
        assertThat(addCount.get()).isEqualTo(tryCount);
    }
}
