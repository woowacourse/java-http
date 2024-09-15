package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("세션 매니저 테스트")
class SessionManagerTest {

    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
    }

    @DisplayName("동시에 세션을 안전하게 추가하고 찾고 삭제한다.")
    @Test
    void testConcurrentAccess() {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        //when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(this::addRemoveSession);
        }
        executorService.shutdown();

        // then
        assertThat(sessionManager.findSession(UUID.randomUUID().toString())).isNull();
    }

    private void addRemoveSession() {
        HttpSession session = Session.createRandomSession();
        sessionManager.add(session);

        HttpSession foundSession = sessionManager.findSession(session.getId());
        assertThat(foundSession).isNotNull();

        sessionManager.remove(session);
        assertThat(sessionManager.findSession(session.getId())).isNull();
    }
}
