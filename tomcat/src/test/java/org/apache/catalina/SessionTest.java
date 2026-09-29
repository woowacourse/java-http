package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import org.junit.jupiter.api.Test;

public class SessionTest {

    @Test
    public void session_success_save_and_inquiry() {
        // given
        final Session session = new Session("session-id");
        final User user = new User(
                "gugu",
                "password",
                "gugu@email.com"
        );

        // when
        session.setAttribute("user", user);

        //then
        assertThat(session.getAttribute("user"))
                .isEqualTo(user);
    }
}
