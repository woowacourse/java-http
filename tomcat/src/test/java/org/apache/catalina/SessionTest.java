package org.apache.catalina;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void 이름으로_값을_저장하고_조회한다() {
        Session session = new Session("session-id");
        Object value = new Object();

        session.setAttribute("key", value);

        assertThat(session.getAttribute("key"))
                .isSameAs(value);
    }

    @Test
    void 저장한_값을_제거하면_조회되지_않는다() {
        Session session = new Session("session-id");
        session.setAttribute("key", new Object());

        session.removeAttribute("key");

        assertThat(session.getAttribute("key"))
                .isNull();
    }
}
