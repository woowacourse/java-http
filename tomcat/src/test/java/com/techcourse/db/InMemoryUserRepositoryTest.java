package com.techcourse.db;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @Test
    void 유저_계정으로_유저를_조회한다() {
        // given
        String account = "gugu";

        // when
        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        // then
        assertThat(user).isNotEmpty();
    }

    @Test
    void 유저_계정_정보로_유저를_등록한다() {
        // given
        String account = "test";
        String password = "test";
        String email = "test@test.com";
        User user = new User(account, password, email);

        // when
        InMemoryUserRepository.save(user);

        // then
        Optional<User> savedUser = InMemoryUserRepository.findByAccount(account);

        assertThat(savedUser).isNotEmpty();
    }

    @Test
    void 등록된_유저는_ID값이_자동할당된다() {
        // given
        long expectId = 3L;

        String account = "test";
        String password = "test";
        String email = "test@test.com";
        User user = new User(account, password, email);

        // when
        InMemoryUserRepository.save(user);

        // then
        Optional<User> savedUser = InMemoryUserRepository.findByAccount(account);
        assertThat(savedUser.get().getId()).isEqualTo(expectId);
    }
}
