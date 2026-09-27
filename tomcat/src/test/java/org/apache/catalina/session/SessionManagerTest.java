package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    private final SessionManager sessionManager = SessionManager.getInstance();

    @Test
    void 세션_ID가_없으면_새_세션을_만든다() {
        Session session = sessionManager.findOrCreate(null);

        assertThat(session.isNew()).isTrue();
        assertThat(sessionManager.findSession(session.getId())).isSameAs(session);
    }

    @Test
    void 저장된_세션_ID면_기존_세션을_반환한다() {
        Session created = sessionManager.findOrCreate(null);

        Session found = sessionManager.findOrCreate(created.getId());

        assertThat(found).isSameAs(created);
        assertThat(found.isNew()).isFalse();
    }

    @Test
    void 서버에_없는_세션_ID면_새_세션을_만든다() {
        Session session = sessionManager.findOrCreate("expired-session-id");

        assertThat(session.isNew()).isTrue();
        assertThat(session.getId()).isNotEqualTo("expired-session-id");
    }

    @Test
    void 세션_ID가_null이면_찾지_못한다() {
        assertThat(sessionManager.findSession(null)).isNull();
    }

    @Test
    void 여러_스레드가_동시에_세션을_만들어도_모두_저장된다() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);
        Set<String> sessionIds = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    startLatch.await();
                    sessionIds.add(sessionManager.findOrCreate(null).getId());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }
        startLatch.countDown();
        doneLatch.await(5, TimeUnit.SECONDS);
        executorService.shutdown();

        assertThat(sessionIds).hasSize(threadCount);
        assertThat(sessionIds).allSatisfy(id -> assertThat(sessionManager.findSession(id)).isNotNull());
    }
}
