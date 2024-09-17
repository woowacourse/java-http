package com.techcourse.db;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("인메모리 데이터베이스 테스트")
class InMemoryUserRepositoryTest {

    private User user = new User(2L, "account", "password", "email@test.com");

    @AfterEach
    void tearDown() {
        InMemoryUserRepository.deleteByAccount(user.getAccount());
    }

    @DisplayName("유저를 저장할 수 있다")
    @Test
    void saveUser() {
        // given
        InMemoryUserRepository.save(user);
        // when & then
        assertThat(InMemoryUserRepository.findByAccount(user.getAccount())).isPresent();
    }

    @DisplayName("account로 유저를 조회할 수 있다")
    @Test
    void findByAccount() {
        // given
        InMemoryUserRepository.save(user);
        // when & then
        Assertions.assertAll(
                () -> assertThat(InMemoryUserRepository.findByAccount(user.getAccount())).isPresent(),
                () -> assertThat(InMemoryUserRepository.findByAccount("nonExist")).isEmpty()
        );
    }

    @DisplayName("account와 password로 유저를 조회할 수 있다.")
    @Test
    void findByAccountAndPassword() {
        // given
        InMemoryUserRepository.save(user);
        // when & then
        Assertions.assertAll(
                () -> assertThat(InMemoryUserRepository.findByAccountAndPassword("account", "password")).isPresent(),
                () -> assertThat(InMemoryUserRepository.findByAccountAndPassword("account", "wrong")).isEmpty()
        );
    }

    @DisplayName("account로 유저 정보를 데이터베이스에서 지울 수 있다")
    @Test
    void cleanUp() {
        // given
        InMemoryUserRepository.save(user);
        // when
        InMemoryUserRepository.deleteByAccount(user.getAccount());
        // then
        assertThat(InMemoryUserRepository.findByAccount(user.getAccount()).isEmpty());
    }
}
