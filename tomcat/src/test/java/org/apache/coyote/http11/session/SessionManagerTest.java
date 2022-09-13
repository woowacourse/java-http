package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
    }

    @DisplayName("SessionManager에 세션을 추가한다.")
    @Test
    void add() throws IOException {
        HttpSession session = new HttpSession();
        String sessionID = session.getId();
        sessionManager.add(session);

        assertThat(sessionManager.findSession(sessionID)).isNotNull();
    }

    @DisplayName("SessionManager로부터 세션을 찾는다.")
    @Test
    void findSession() throws IOException {
        HttpSession sessionA = new HttpSession();
        HttpSession sessionB = new HttpSession();
        String foundID = sessionA.getId();

        sessionManager.add(sessionA);
        sessionManager.add(sessionB);

        assertThat(sessionManager.findSession(foundID)).isSameAs(sessionA);
    }

    @DisplayName("SessionManager에 등록된 세션을 제거한다.")
    @Test
    void remove() throws IOException {
        HttpSession sessionA = new HttpSession();
        HttpSession sessionB = new HttpSession();
        String deletedID = sessionA.getId();

        sessionManager.add(sessionA);
        sessionManager.add(sessionB);
        sessionManager.remove(sessionA);

        assertThat(sessionManager.findSession(deletedID)).isNull();

    }
}