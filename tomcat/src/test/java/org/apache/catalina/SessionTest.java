package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void 이름으로_저장한_사용자를_조회한다() {
        Session session = new Session("session-1");
        User user = new User(1L, "gugu", "password", "gugu@example.com");

        session.setAttribute("user", user);

        assertThat(session.getAttribute("user")).isSameAs(user);
    }

    @Test
    void 저장한_값을_제거하면_조회되지_않는다() {
        Session session = new Session("session-1");
        session.setAttribute("user", new User(1L, "gugu", "password", "gugu@example.com"));

        session.removeAttribute("user");

        assertThat(session.getAttribute("user")).isNull();
    }
}
