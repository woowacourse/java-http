package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {

    @Test
    @DisplayName("세션의 속성값 조회")
    void getAttribute() {
        Session session = new Session("1");
        session.setAttribute("1", "value1");
        session.setAttribute("2", "value2");

        assertThat(session.getAttribute("1")).isEqualTo("value1");
    }
}
