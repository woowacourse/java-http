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
        SessionManager.add(session);
        String invalidSessionId = "invalidSessionId";

        assertAll(
                () -> assertThat(SessionManager.isValid(session.getId())).isTrue(),
                () -> assertThat(SessionManager.isValid(invalidSessionId)).isFalse()
        );
    }
}