package com.techcourse.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class SessionServiceTest {

    @Nested
    class 세션 {

        @Test
        void 세션이_성공적으로_등록되고_확인된다() {
            // given
            User user = new User("test", "1234", "test@test.com");
            String cookie = SessionService.createCookie(user);

            // when & then
            assertThat(SessionService.hasSession(cookie)).isTrue();
        }

        @Test
        void 존재하지_않는_세션을_확인할_수_없다() {
            // given
            User user = new User("test", "1234", "test@test.com");
            SessionService.createCookie(user);
            String NoCookie = "noCookie";

            // when & then
            assertThat(SessionService.hasSession(NoCookie)).isFalse();
        }
    }
}
