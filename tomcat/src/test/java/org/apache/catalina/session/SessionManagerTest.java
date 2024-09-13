package org.apache.catalina.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    @DisplayName("같은 아이디의 세션이 있다면 저장하지 않는다.")
    @Test
    void add() throws InterruptedException {
        SessionManager sessionManager = SessionManager.getInstance();
        Session session1 = new Session("session");
        Session session2 = new Session("session");
        final var firstThread = new Thread(() -> sessionManager.add(session1));
        final var secondThread = new Thread(() -> sessionManager.add(session2));

        firstThread.start();
        secondThread.start();
        secondThread.join();
        firstThread.join();

        assertThat(sessionManager.findSession("session")).isEqualTo(session1);
    }
}
