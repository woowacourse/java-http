package com.techcourse.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.model.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("사용자 레포 테스트")
class InMemoryUserRepositoryTest {

    @DisplayName("사용자 저장에 성공한다.")
    @Test
    void save() {
        // given
        long id = 1L;
        String account = "account";
        String password = "password";
        String email = "email@email.com";

        User user = new User(id, account, password, email);

        // when
        InMemoryUserRepository.save(user);

        // then
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(account);

        assertAll(
                () -> assertThat(optionalUser).isPresent(),
                () -> assertThat(optionalUser.get().checkPassword(password)).isTrue()
        );
    }
}
