package com.techcourse.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.catalina.auth.Session;
import org.apache.catalina.auth.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

class AuthServiceTest {

    @Nested
    @DisplayName("로그인 상태 확인")
    class isLogin {
        @Test
        @DisplayName("성공 : 세션을 확인해 로그인한 상태면 true")
        void isLoginTrue() {
            String id = "34dr3-fsdf3-34";
            Session session = new Session(id);
            SessionManager.getInstance().add(session);
            AuthService authService = new AuthService();

            boolean actual = authService.isLogin(id);

            assertThat(actual).isTrue();
        }

        @Test
        @DisplayName("성공 : 세션을 확인해 로그인 안 한 상태면 false")
        void isLoginFalse() {
            String id = "34dr3-fsdf3-34";
            AuthService authService = new AuthService();

            boolean actual = authService.isLogin(id);

            assertThat(actual).isTrue();
        }
    }

    @Nested
    @DisplayName("세션 생성")
    class createSession {

        @Test
        @DisplayName("성공 : 세션 생성 성공")
        void createSessionSuccess() {
            User user = new User("kyum", "password", "kyum@naver.com");
            InMemoryUserRepository.save(user);
            AuthService authService = new AuthService();

            Session session = authService.createSession(user);

            assertThat(session.getAttribute(session.getId())).isEqualTo(user);
        }
    }
}
