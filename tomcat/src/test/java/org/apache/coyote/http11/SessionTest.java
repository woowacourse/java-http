package org.apache.coyote.http11;

import com.techcourse.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SessionTest {

    @Test
    void storesAndReadsAttributes() {
        final Session session = new Session("session-id");
        final User user = new User("gugu", "password", "gugu@example.com");

        session.setAttribute("user", user);

        assertThat(session.getId()).isEqualTo("session-id");
        assertThat(session.getAttribute("user")).isEqualTo(user);
    }

    @Test
    void rejectsAccessAfterInvalidation() {
        final Session session = new Session("session-id");
        session.setAttribute("user", new User("gugu", "password", "gugu@example.com"));

        session.invalidate();

        assertThatThrownBy(() -> session.getAttribute("user"))
                .isInstanceOf(IllegalStateException.class);
    }
}
