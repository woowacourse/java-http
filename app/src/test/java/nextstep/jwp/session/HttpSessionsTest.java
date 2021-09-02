package nextstep.jwp.session;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpSessionsTest {

    @Test
    void getSession() {
        String sessionId = String.valueOf(UUID.randomUUID());
        HttpSession createdSession = HttpSessions.getSession(sessionId);
        HttpSession foundSession = HttpSessions.getSession(sessionId);
        assertEquals(createdSession, foundSession);
    }
}