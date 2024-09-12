package org.apache.catalina;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    @DisplayName("세션 추가 성공")
    @Test
    void add() {
        // given
        SessionManager sessionManager = new SessionManager();
        int size = sessionManager.size();

        // when
        sessionManager.add(Session.createRandomSession());

        // then
        assertThat(sessionManager.size()).isEqualTo(size + 1);
    }

    @DisplayName("세션 찾기 성공")
    @Test
    void findSession() throws IOException {
        // given
        SessionManager sessionManager = new SessionManager();
        Session session = Session.createRandomSession();
        sessionManager.add(session);

        // when & then
        assertThat(sessionManager.findSession(session.getId())).isEqualTo(session);
    }


    @DisplayName("세션 삭제 성공")
    @Test
    void remove() throws IOException {
        // given
        SessionManager sessionManager = new SessionManager();
        Session session = Session.createRandomSession();
        sessionManager.add(session);
        int size = sessionManager.size();

        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.size()).isEqualTo(size - 1);
    }
}
