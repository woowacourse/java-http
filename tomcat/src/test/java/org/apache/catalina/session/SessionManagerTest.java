package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @DisplayName("세션을 추가한다.")
    @Test
    void addTest() {
        // given
        SessionManager sessionManager = new SessionManager();
        Session session = new Session("testSession");
        session.setAttribute("username", "mangcho");

        // when
        sessionManager.add(session);

        // then
        assertThat(sessionManager.findSession("testSession")).isEqualTo(session);
    }

    @DisplayName("세션을 삭제한다.")
    @Test
    void removeTest() {
        // given
        SessionManager sessionManager = new SessionManager();
        Session session = new Session("testSession");
        session.setAttribute("username", "mangcho");
        sessionManager.add(session);

        // when
        sessionManager.remove("testSession");

        // then
        assertThat(sessionManager.findSession("testSession")).isNull();
    }
}
