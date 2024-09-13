package com.techcourse.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.TechcourseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTest {

    private final UserService userService = UserService.getInstance();

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.clear();
    }

    @Test
    void 올바른_계정과_비밀번호로_로그인에_성공한다() {
        // Given
        String account = "gugu";
        String password = "password";

        // When
        User user = userService.login(account, password);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getAccount()).isEqualTo(account);
    }

    @Test
    void 잘못된_비밀번호로_로그인하면_예외가_발생한다() {
        // Given
        String account = "gugu";
        String wrongPassword = "wrongPassword";

        // When & Then
        assertThatThrownBy(() -> userService.login(account, wrongPassword))
                .isInstanceOf(TechcourseException.class)
                .hasMessageContaining("계정 정보가 틀렸습니다.");
    }

    @Test
    void 존재하지_않는_계정으로_로그인하면_예외가_발생한다() {
        // Given
        String nonExistentAccount = "nonExistentUser";
        String password = "password";

        // When & Then
        assertThatThrownBy(() -> userService.login(nonExistentAccount, password))
                .isInstanceOf(TechcourseException.class)
                .hasMessageContaining("계정 정보가 틀렸습니다.");
    }

    @Test
    void 새로운_계정으로_회원가입에_성공한다() {
        // Given
        String newAccount = "dora";
        String password = "password";
        String email = "dora@example.com";

        // When
        userService.register(newAccount, password, email);

        // Then
        User registeredUser = InMemoryUserRepository.findByAccount(newAccount).orElse(null);
        assertThat(registeredUser).isNotNull();
        assertThat(registeredUser.getAccount()).isEqualTo(newAccount);
    }

    @Test
    void 이미_존재하는_계정으로_회원가입하면_예외가_발생한다() {
        // Given
        String existingAccount = "gugu";
        String password = "password";
        String email = "user@example.com";

        // When & Then
        assertThatThrownBy(() -> userService.register(existingAccount, password, email))
                .isInstanceOf(TechcourseException.class)
                .hasMessageContaining("이미 존재하는 계정입니다.");
    }
}
