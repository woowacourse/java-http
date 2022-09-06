package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    void 세션에_값을_저장한다() {
        // given
        Session session = new Session("id");

        // when
        session.setAttribute("name", "value");

        // then
        assertThat(session.getAttribute("name")).isEqualTo("value");
    }

    @Test
    void 세션에서_값을_제거한다() {
        // given
        Session session = new Session("id");
        session.setAttribute("name", "value");

        // when
        session.removeAttribute("name");

        // then
        assertThat(session.getAttribute("name")).isNull();
    }
}
