package org.apache.catalina.session;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionServiceTest {

    HttpSession session1 = new Session("1");
    HttpSession session2 = new Session("2");
    private final Manager sessionManager = new SessionManager();
    private final SessionService sessionService = new SessionService();

    @AfterEach
    void rollbackSession() {
        sessionManager.remove(session1);
        sessionManager.remove(session2);
    }

    @DisplayName("세션이 관리되고 있는 경우 true를 반환한다.")
    @Test
    void isManagedSessionTrue() {
        sessionManager.add(session1);
        assertTrue(sessionService.isManagedSession(session1));
    }

    @DisplayName("세션이 관리되지 않고 있는 경우 false를 반환한다.")
    @Test
    void isManagedSessionFalse() {
        assertFalse(sessionService.isManagedSession(session2));
    }
}
