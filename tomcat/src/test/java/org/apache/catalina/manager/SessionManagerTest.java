package org.apache.catalina.manager;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SessionManagerTest {

    private static final int THREAD_COUNT = 100;
    private static final int SESSIONS_PER_THREAD = 1000;

    @Test
    public void testHashMapConcurrencyIssueInSessionManager() throws InterruptedException {
        SessionManager sessionManager = SessionManager.getInstance();
        int beforeSize = sessionManager.findAllSessions().size();

        Thread[] threads = new Thread[THREAD_COUNT];

        // 각 스레드에서 세션을 생성하고 세션 매니저에 추가
        for (int i = 0; i < THREAD_COUNT; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < SESSIONS_PER_THREAD; j++) {
                    Session session = new Session();
                    sessionManager.add(session);
                }
            });
        }

        // 모든 스레드 시작
        for (Thread thread : threads) {
            thread.start();
        }

        // 모든 스레드가 작업을 완료할 때까지 대기
        for (Thread thread : threads) {
            thread.join();
        }

        // 실제로 추가된 세션의 개수가 기대한 값과 같은지 확인
        int expectedSize = THREAD_COUNT * SESSIONS_PER_THREAD + beforeSize;
        int actualSize = sessionManager.findAllSessions().size();
        assertThat(actualSize).isEqualTo(expectedSize);
    }
}
