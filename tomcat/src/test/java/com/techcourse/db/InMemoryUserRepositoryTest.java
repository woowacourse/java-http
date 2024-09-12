package com.techcourse.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @DisplayName("사용자를 저장한다.")
    @Test
    void saveTest() {
        // given
        User user = new User("mangcho", "password", "hello@woowa.net");

        // when
        InMemoryUserRepository.save(user);

        // then
        assertThat(InMemoryUserRepository.findByAccount("mangcho")).isPresent();
    }

    @DisplayName("사용자를 찾는다.")
    @Test
    void findByAccountTest() {
        // when
        User user = InMemoryUserRepository.findByAccount("gugu").orElseThrow();

        // then
        assertAll(
                () -> assertThat(user.getAccount()).isEqualTo("gugu"),
                () -> assertThat(user.checkPassword("password")).isTrue()
        );
    }
}
