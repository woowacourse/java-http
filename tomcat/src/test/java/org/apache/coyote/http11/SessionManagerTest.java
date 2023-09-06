package org.apache.coyote.http11;

import nextstep.jwp.model.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
