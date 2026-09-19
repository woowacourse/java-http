package org.apache.catalina.session;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    @Test
    void 세션_ID를_반환한다() {
        final Session session = new Session("session-id");

        assertThat(session.getId()).isEqualTo("session-id");
    }

    @Test
    void 속성을_저장하고_조회한다() {
        final Session session = new Session("session-id");
        final Object user = new Object();

        session.setAttribute("user", user);

        assertThat(session.getAttribute("user")).isSameAs(user);
    }

    @Test
    void 속성을_삭제한다() {
        final Session session = new Session("session-id");
        session.setAttribute("user", new Object());

        session.removeAttribute("user");

        assertThat(session.getAttribute("user")).isNull();
    }

    @Test
    void 세션을_무효화하면_모든_속성을_삭제한다() {
        final Session session = new Session("session-id");
        session.setAttribute("user", new Object());
        session.setAttribute("cart", new Object());

        session.invalidate();

        assertThat(session.getAttribute("user")).isNull();
        assertThat(session.getAttribute("cart")).isNull();
    }
}
