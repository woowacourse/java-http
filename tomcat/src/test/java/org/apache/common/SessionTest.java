package org.apache.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.UUID;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SessionTest {

    @Test
    void 세션을_생성한다() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);

        assertThat(session.getId()).isEqualTo(sessionId);
    }

    @Test
    void 세션에_값을_저장한다() {
        String sessionId = UUID.randomUUID().toString();
        User user = new User("account", "password", "email@gmail.com");
        Session session = new Session(sessionId);

        session.setAttribute("user", user);

        assertThat(session.getAttribute("user")).isEqualTo(user);
    }

    @Test
    void 세션에_값을_제거한다() {
        String sessionId = UUID.randomUUID().toString();
        User user = new User("account", "password", "email@gmail.com");
        Session session = new Session(sessionId);
        session.setAttribute("user", user);

        session.removeAttribute("user");

        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    void 세션을_초기화한다() {
        String sessionId = UUID.randomUUID().toString();
        User user = new User("account", "password", "email@gmail.com");
        Session session = new Session(sessionId);
        session.setAttribute("user", user);
        session.setAttribute("key", "value");

        session.invalidate();

        assertAll(
                () -> assertThat(session.getAttribute("user")).isNull(),
                () -> assertThat(session.getAttribute("key")).isNull()
        );
    }
}
