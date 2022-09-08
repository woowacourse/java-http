package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void storeUser() {
        UUID uuid = UUID.randomUUID();
        Session session = new Session(uuid.toString());
        User user = new User("maru", "password", "maru@naver.com");
        session.setAttribute("user", user);

        assertThat(session.getAttribute("user")).isNotNull();
    }
}