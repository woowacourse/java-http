package org.apache.coyote.session;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SessionManagerTest {

    @Test
    @DisplayName("Session Id를 키로, 저장할 수 았다.")
    void addSession() {
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = SessionPrefix.SESSION;

        sessionManager.add(session);

        assertEquals(sessionManager.findSession(session.getId()), session);
    }
}
