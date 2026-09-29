package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void 새로운_세션을_생성하고_저장한다() {
        // given
        final SessionManager sessionManager = SessionManager.getInstance();

        // when
        final Session session = sessionManager.createNewSession();

        // then
        assertThat(sessionManager.findSession(session.id())).isSameAs(session);
        sessionManager.remove(session.id());
    }

    @Test
    void 세션_ID_없이_조회하면_null을_반환한다() {
        // given
        final SessionManager sessionManager = SessionManager.getInstance();

        // when & then
        assertThat(sessionManager.findSession(null)).isNull();
    }

    @Test
    void 여러_스레드가_동시에_생성한_세션을_모두_저장한다() {
        // given
        final int sessionCount = 100;
        final SessionManager sessionManager = SessionManager.getInstance();
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        final CountDownLatch startSignal = new CountDownLatch(1);
        final List<Session> sessions = new ArrayList<>();

        try {
            final List<Future<Session>> futures = IntStream.range(0, sessionCount)
                .mapToObj(index -> executorService.submit(() -> {
                    startSignal.await();
                    return sessionManager.createNewSession();
                })).toList();

            // when
            startSignal.countDown();
            futures.stream()
                .map(this::getResult)
                .forEach(sessions::add);

            // then
            assertThat(sessions)
                .extracting(Session::id)
                .doesNotHaveDuplicates();
            assertThat(sessions)
                .allSatisfy(session ->
                    assertThat(sessionManager.findSession(session.id())).isSameAs(session));
        } finally {
            sessions.forEach(session -> sessionManager.remove(session.id()));
            executorService.shutdownNow();
        }
    }

    @Test
    void 여러_스레드가_하나의_세션에_추가한_속성을_모두_저장한다() {
        // given
        final int attributeCount = 100;
        final Session session = Session.init("session-id");
        final ExecutorService executorService = Executors.newFixedThreadPool(10);
        final CountDownLatch startSignal = new CountDownLatch(1);

        try {
            final List<Future<Object>> futures = IntStream.range(0, attributeCount)
                .mapToObj(index -> executorService.submit(() -> {
                    startSignal.await();
                    session.addAttribute("key-" + index, "value-" + index);
                    return null;
                }))
                .toList();

            // when
            startSignal.countDown();
            futures.forEach(this::getResult);

            // then
            for (int index = 0; index < attributeCount; index++) {
                assertThat(session.getAttribute("key-" + index))
                    .isEqualTo("value-" + index);
            }
        } finally {
            executorService.shutdownNow();
        }
    }

    private <T> T getResult(final Future<T> future) {
        try {
            return future.get();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
