package org.apache.catalina;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    @Test
    void JSESSIONID가_없으면_세션_조회_결과가_비어있다() {
        // given
        String sessionId = null;

        // when
        Optional<Session> session = SessionManager.find(sessionId);

        // then
        assertThat(session).isEmpty();
    }

    @Test
    void 생성한_세션의_ID로_조회하면_동일한_세션을_반환한다() {
        // given
        Session createdSession = SessionManager.create();

        // when
        Session foundSession = SessionManager.find(createdSession.getId()).orElseThrow();

        // then
        assertThat(foundSession).isSameAs(createdSession);
    }

    @Test
    void 등록되지_않은_JSESSIONID로_조회하면_세션_조회_결과가_비어있다() {
        // given
        String sessionId = "unknown-session-id";

        // when
        Optional<Session> session = SessionManager.find(sessionId);

        // then
        assertThat(session).isEmpty();
    }

    @Test
    void 세션을_생성하면_UUID를_세션_ID로_부여한다() {
        // when
        Session session = SessionManager.create();

        // then
        assertThatValueIsUuid(session.getId());
    }

    @Test
    void 세션_attribute를_저장하고_조회한다() {
        // given
        Session session = SessionManager.create();

        // when
        session.setAttribute("user", "usher");

        // then
        assertThat(session.getAttribute("user")).isEqualTo("usher");
    }

    @Test
    void 여러_스레드에서_동시에_만든_세션을_각_ID로_찾을_수_있다() throws Exception {
        // given
        int sessionCount = 32;
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService workers = Executors.newFixedThreadPool(4);

        try {
            List<Future<Session>> results = IntStream.range(0, sessionCount)
                    .mapToObj(ignored -> workers.submit(() -> {
                        start.await();
                        return SessionManager.create();
                    }))
                    .toList();

            // when
            start.countDown();

            // then
            for (Future<Session> result : results) {
                Session session = result.get(5, TimeUnit.SECONDS);
                assertThat(SessionManager.find(session.getId())).containsSame(session);
            }
        } finally {
            start.countDown();
            workers.shutdownNow();
        }
    }

    private void assertThatValueIsUuid(String value) {
        assertThat(UUID.fromString(value)).isNotNull();
    }
}
