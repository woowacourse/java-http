package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("세션")
class SessionTest {

    @Test
    @DisplayName("생성할 때 전달받은 ID를 반환한다")
    void returnsGivenId() {
        // given
        final HttpSession session = new Session("session-id");

        // when
        final String id = session.getId();

        // then
        assertThat(id).isEqualTo("session-id");
    }

    @Test
    @DisplayName("속성을 이름으로 저장하고 조회한다")
    void storesAndGetsAttribute() {
        // given
        final HttpSession session = new Session("session-id");

        // when
        session.setAttribute("loginUser", "yongsung");

        // then
        assertThat(session.getAttribute("loginUser")).isEqualTo("yongsung");
    }

    @Test
    @DisplayName("저장된 속성을 이름으로 제거한다")
    void removesAttribute() {
        // given
        final HttpSession session = new Session("session-id");
        session.setAttribute("loginUser", "yongsung");

        // when
        session.removeAttribute("loginUser");

        // then
        assertThat(session.getAttribute("loginUser")).isNull();
    }
}
