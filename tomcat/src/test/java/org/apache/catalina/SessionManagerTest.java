package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionManagerTest {

    @DisplayName("유효현 SessionId인지 반환한다.")
    @Test
    void isValid() {
        User user = new User("chris", "password", "email@google.com");
        Session session = new Session("user", user);
        Manager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        String invalidSessionId = "invalidSessionId";

        assertAll(
                () -> assertThat(sessionManager.findSession(session.getId())).isEqualTo(session),
                () -> assertThat(sessionManager.findSession(invalidSessionId)).isNull()
        );
    }
}