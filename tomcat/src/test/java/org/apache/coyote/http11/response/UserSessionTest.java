package org.apache.coyote.http11.response;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserSessionTest {

    @Test
    @DisplayName("유저의 세션이 존재하는 지 조회할 수 있다 - false")
    void exist_fail() {
        //when
        final boolean exist = UserSession.exist("nothing");

        //then
        assertThat(exist).isFalse();
    }

    @Test
    @DisplayName("유저의 세션이 존재하는 지 조회할 수 있다 - true")
    void exist_true() {
        //given
        UserSession.login("test", new User("id", "password", ""));

        //when
        final boolean exist = UserSession.exist("test");

        //then
        assertThat(exist).isTrue();
    }

    @Test
    @DisplayName("로그인을 해서 세션에 유저를 추가할 수 있다")
    void login() {
        //given
        final String sessionId = UUID.randomUUID().toString();
        final User user = new User("연", "어", "연어");

        //when
        UserSession.login(sessionId, user);

        //then
        assertThat(UserSession.exist(sessionId)).isTrue();
    }
}
