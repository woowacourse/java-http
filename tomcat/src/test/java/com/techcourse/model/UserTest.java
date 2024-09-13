package com.techcourse.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @DisplayName("회원 정보에 빈 문자를 입력했을 경우 예외가 발생한다.")
    @Test
    void createFailByBlank() {
        assertAll(
                () -> assertThatThrownBy(() -> new User(1L, "", "1234", "kaki@email.com"))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> new User(1L, "kaki", " ", "kaki@email.com"))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> new User(1L, "kaki", "1234", null))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }

    @DisplayName("비밀번호가 일치하면 true를 반환하다.")
    @Test
    void checkPasswordTrue() {
        User user = new User(1L, "kaki", "1234", "kaki@email.com");
        InMemoryUserRepository.save(user);
        User findUser = InMemoryUserRepository.findByAccount("kaki").get();

        boolean valid = findUser.checkPassword("1234");

        assertThat(valid).isTrue();
    }

    @DisplayName("비밀번호가 일치하지 않으면 false를 반환하다.")
    @Test
    void checkPasswordFalse() {
        User user = new User(1L, "kaki", "1234", "kaki@email.com");
        InMemoryUserRepository.save(user);
        User findUser = InMemoryUserRepository.findByAccount("kaki").get();

        boolean valid = findUser.checkPassword("abcd");

        assertThat(valid).isFalse();
    }
}
