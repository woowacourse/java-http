package org.apache.coyote.http;

import nextstep.jwp.model.User;
import org.apache.coyote.http11.SessionManager;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionManagerTest {

    @Test
    void add() {
        SessionManager.add("sessionId", new User("moomin", "password","email"));

        assertTrue(SessionManager.isAlreadyLogin("sessionId"));
    }

    @Test
    void isAlreadyLogin() {
        assertThat(SessionManager.isAlreadyLogin("nonSessionId")).isFalse();
    }

}
