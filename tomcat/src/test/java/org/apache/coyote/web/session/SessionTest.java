package org.apache.coyote.web.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void setAttribute() {
        Session session = new Session("id");
        User user = new User("huni", "password", "huni@huni.com");
        session.setAttribute("user", user);

        assertThat(session.getAttribute("user").get()).usingRecursiveComparison()
                .comparingOnlyFields("account", "password", "email")
                .isEqualTo(user);
    }

    @Test
    void remoteAttribute() {
        Session session = new Session("id");
        User user = new User("huni", "password", "huni@huni.com");
        session.setAttribute("user", user);

        session.removeAttribute("user");
        assertThat(session.getValues()).isEmpty();
    }

    @Test
    void invalidate() {
        Session session = new Session("id");
        User user = new User("huni", "password", "huni@huni.com");
        session.setAttribute("user", user);
        SessionManager.addSession("id", session);

        session.invalidate();
        assertAll(
                () -> assertThat(session.getValues()).isEmpty(),
                () -> assertThat(SessionManager.findSession("id")).isEmpty()
        );
    }
}
