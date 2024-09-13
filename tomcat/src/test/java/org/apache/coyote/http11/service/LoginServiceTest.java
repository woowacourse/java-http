package org.apache.coyote.http11.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginServiceTest {

    private final LoginService loginService = LoginService.getInstance();

    @AfterEach
    void tearDown() {
        InMemoryUserRepository.clear();
    }

    @DisplayName("로그인 성공 여부를 체크한다")
    @Test
    void checkLoginSuccess() {
        User user = new User("testAccount", "testPassword", "testEmail");
        InMemoryUserRepository.save(user);

        assertThatCode(() -> loginService.checkLogin("testAccount", "testPassword"))
                .doesNotThrowAnyException();
    }

    @DisplayName("로그인 실패 시, SecurityException을 반환한다")
    @Test
    void checkLoginFail() {
        User user = new User("testAccount", "testPassword", "testEmail");
        InMemoryUserRepository.save(user);

        assertThatThrownBy(() -> loginService.checkLogin("testAccount2", "testPassword2"))
                .isInstanceOf(SecurityException.class);
    }

    @DisplayName("Account 정보를 바탕으로 유저를 찾는다")
    @Test
    void findByAccountSuccess() {
        User user = new User("testAccount", "testPassword", "testEmail");
        InMemoryUserRepository.save(user);

        User foundUser = loginService.findByAccount("testAccount");

        assertThat(user).isEqualTo(foundUser);
    }

    @DisplayName("유저가 존재하지 않을 경우 SecurityException을 반환한다")
    @Test
    void findByAccountFailWhenUserNotExists() {
        String account = "testAccount";
        String otherAcoount = "otherAccount";
        User user = new User(account, "testPassword", "testEmail");
        InMemoryUserRepository.save(user);

        assertThatThrownBy(() -> loginService.findByAccount(otherAcoount))
                .isInstanceOf(SecurityException.class);
    }
}
