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

    @DisplayName("값을 세선에서 삭제한다.")
    @Test
    void removeAttribute() {
        Session session = new Session("123");
        session.setAttribute("name", "value");
        session.removeAttribute("name");

        assertThat(session.getAttribute("name")).isEqualTo(null);
    }

    @DisplayName("세션을 모두 삭제한다.")
    @Test
    void invalidateSession() {
        Session session = new Session("123");
        session.setAttribute("name", "value");
        session.setAttribute("name2", "value2");

        session.invalidate();

        assertAll(
                () -> assertThat(session.getAttribute("name")).isEqualTo(null),
                () -> assertThat(session.getAttribute("name2")).isEqualTo(null)
        );
    }
}
