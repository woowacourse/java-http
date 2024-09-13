package org.apache.catalina.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionTest {
    @Test
    @DisplayName("성공 : key에 해당하는 attribute를 조회 가능")
    void getAttribute() {
        Session session = new Session("id");
        session.setAttribute("name", "value");

        String actual = (String) session.getAttribute("name");

        assertThat(actual).isEqualTo("value");
    }

    @Test
    @DisplayName("성공 : attribute를 추가 가능")
    void setAttribute() {
        Session session = new Session("id");

        session.setAttribute("name", "value");

        Map<String, Object> actual = session.getValues();
        assertThat(actual).isEqualTo(Map.of("name", "value"));
    }

    @Test
    @DisplayName("성공 : attribute를 삭제 가능")
    void removeAttribute() {
        Session session = new Session("id");
        session.setAttribute("name", "value");

        session.removeAttribute("name");

        Map<String, Object> actual = session.getValues();
        assertThat(actual).isEqualTo(Map.of());
    }
}
