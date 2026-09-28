package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void 속성을_null로_변경하면_삭제된다() {
        Session session = new Session("session-id");
        session.setAttribute("user", "gugu");

        session.setAttribute("user", null);

        assertThat(session.getAttribute("user")).isNull();
    }
}
