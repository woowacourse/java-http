package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.model.User;

class SessionTest {

    @Test
    @DisplayName("Attribute을 설정한다.")
    void setAttribute() {
        // given
        final String sessionId = "testId";
        final Session session = new Session(sessionId);

        // when
        session.setAttribute("user", new User(1L, "account", "password", "email"));
        final User user = (User)session.getAttribute("user");
        final String actual = user.getAccount();

        // then
        assertThat(actual).isEqualTo("account");
    }

    @Test
    @DisplayName("없는 Attribute를 조회할 경우 예외를 반환한다.")
    void getAttribute_exception() {
        // given
        final String sessionId = "testId";
        final Session session = new Session(sessionId);

        // when, then
        assertThatThrownBy(() -> session.getAttribute("user"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("No Such Attribute exists in this Session");

    }
}