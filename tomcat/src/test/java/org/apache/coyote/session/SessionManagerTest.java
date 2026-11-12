package org.apache.coyote.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @Test
    void 세션_아이디가_null이면_조회되지_않는다() {
        assertThat(SessionManager.findSession(null)).isNull();
    }

    @Test
    void 여러_스레드에서_세션을_저장하고_조회한다() throws Exception {
        var executor = Executors.newFixedThreadPool(8);
        List<Callable<Session>> tasks = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            String id = "concurrent-session-" + i;
            tasks.add(() -> {
                Session session = new Session(id);
                SessionManager.add(session);
                assertThat(SessionManager.findSession(id)).isSameAs(session);
                return session;
            });
        }
        try {
            List<Future<Session>> results = executor.invokeAll(tasks);
            for (Future<Session> result : results) {
                Session session = result.get();
                assertThat(SessionManager.findSession(session.getId())).isSameAs(session);
            }
        } finally {
            executor.shutdownNow();
            for (int i = 0; i < 200; i++) {
                SessionManager.remove("concurrent-session-" + i);
            }
        }
    }

    @Test
    void 세션을_저장하고_조회한다() {
        // given
        final var session = new Session("session-manager-add");

        // when
        SessionManager.add(session);

        // then
        assertThat(SessionManager.findSession("session-manager-add")).isSameAs(session);

        // cleanup
        SessionManager.remove("session-manager-add");
    }

    @Test
    void 존재하지_않는_세션을_조회하면_null을_반환한다() {
        // when & then
        assertThat(SessionManager.findSession("unknown-session")).isNull();
    }

    @Test
    void 세션을_삭제한다() {
        // given
        final var session = new Session("session-manager-remove");
        SessionManager.add(session);

        // when
        SessionManager.remove("session-manager-remove");

        // then
        assertThat(SessionManager.findSession("session-manager-remove")).isNull();
    }
}
