package org.apache.catalina.startup;

import common.http.Session;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SessionManagerTest {

    @Test
    void Session을_추가한다() {
        // given
        Session session = new Session("로이스");
        SessionManager sessionManager = new SessionManager();

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSession("로이스")).isEqualTo(session);
    }

    @Test
    void Sessoin을_삭제한다() {
        // given
        Session session = new Session("로이스");
        SessionManager sessionManager = new SessionManager();
        sessionManager.add(session);

        // when
        sessionManager.remove(session);

        // then
        assertThat(sessionManager.hasSession(session)).isFalse();
    }
}
