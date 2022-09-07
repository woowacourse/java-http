package org.apache.coyote.common.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Session 은 ")
class SessionTest {

    @DisplayName("원하는 값을 세션을 등록한다.")
    @Test
    void setAttribute() {
        Session session = new Session("123");
        session.setAttribute("name", "value");

        assertThat(session.getAttribute("name")).isEqualTo("value");
    }
}
