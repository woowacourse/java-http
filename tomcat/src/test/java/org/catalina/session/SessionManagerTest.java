package org.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;

public class SessionManagerTest {
    private final SessionManager sessionManager = SessionManager.getInstance();

    @Test
    void 존재하지_않는_ID라면_세션을_생성하고_저장() {
        String sessionId = UUID.randomUUID().toString();

        try {
            assertThat(sessionManager.findSession(sessionId)).isNull();

            Session session = sessionManager.findOrCreateSession(sessionId);

            assertThat(session).isNotNull();
            assertThat(session.getId()).isEqualTo(sessionId);
            assertThat(sessionManager.findSession(sessionId))
                    .isSameAs(session);
        } finally {
            sessionManager.remove(sessionId);
        }
    }

    @Test
    void 같은_ID로_호출하면_기존_세션을_반환() {
        String sessionId = UUID.randomUUID().toString();

        try {
            Session first = sessionManager.findOrCreateSession(sessionId);
            Session second = sessionManager.findOrCreateSession(sessionId);

            assertThat(second).isSameAs(first);
        } finally {
            sessionManager.remove(sessionId);
        }
    }

    @Test
    void 동시에_같은_ID_호출_시_같은_세션_반환() throws Exception {
        String sessionId = UUID.randomUUID().toString();
        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch ready = new CountDownLatch(threadCount);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Session>> results = new ArrayList<>();

        try {
            for (int count = 1; count <= threadCount; count++) {
                results.add(executorService.submit(() -> {
                    ready.countDown();

                    if (!start.await(5, TimeUnit.SECONDS)) {
                        throw new IllegalStateException("출발 신호 대기 시간 초과");
                    }

                    return sessionManager.findOrCreateSession(sessionId);
                }));
            }

            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            Session first = results.getFirst().get(5, TimeUnit.SECONDS);
            for (Future<Session> result : results) {
                assertThat(result.get(5, TimeUnit.SECONDS)).isSameAs(first);
            }

            assertThat(sessionManager.findSession(sessionId)).isSameAs(first);
        } finally {
            start.countDown();
            executorService.shutdownNow();

            try {
                assertThat(executorService.awaitTermination(5, TimeUnit.SECONDS)).isTrue();
            } finally {
                sessionManager.remove(sessionId);
            }
        }
    }
}
