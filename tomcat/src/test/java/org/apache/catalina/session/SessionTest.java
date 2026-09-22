package org.apache.catalina.session;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SessionTest {

    @Test
    void 값을_저장하고_이름으로_꺼낸다() {
        final Session session = new Session("656cef62");
        session.setAttribute("user", "gugu");

        assertThat(session.getAttribute("user")).isEqualTo("gugu");
        assertThat(session.getAttribute("cart")).isNull();
    }

    @Test
    void 값을_비우면_저장된_값이_모두_사라진다() {
        final Session session = new Session("656cef62");
        session.setAttribute("user", "gugu");
        session.setAttribute("locale", "ko");

        session.clear();

        assertThat(session.getAttribute("user")).isNull();
        assertThat(session.getAttribute("locale")).isNull();
    }
}
