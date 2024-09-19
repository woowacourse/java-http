package com.techcourse.db;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @DisplayName("유저를 저장할 수 있다.")
    @Test
    void save() {
        User user = new User("test1", "1234", "test@test.com");
        InMemoryUserRepository.save(user);

        assertThat(InMemoryUserRepository.findByAccount(user.getAccount())).isNotEmpty();
    }

    @DisplayName("이미 존재하는 유저를 저장하면 해당 유저가 업데이트된다.")
    @Test
    void saveExist() {
        InMemoryUserRepository.save(new User("test1", "1234", "test@test.com"));

        InMemoryUserRepository.save(new User("test1", "5678", "change@test.com"));
        Optional<User> findUser = InMemoryUserRepository.findByAccount("test1");
        assertThat(findUser).isNotEmpty();

        User user = findUser.get();
        assertThat(user.getAccount()).isEqualTo("test1");
        assertThat(user.checkPassword("5678")).isTrue();
    }

    @DisplayName("존재하는 유저를 조회하면 유저를 반환한다.")
    @Test
    void findByAccount() {
        Optional<User> gugu = InMemoryUserRepository.findByAccount("gugu");

        assertThat(gugu).isNotEmpty();
    }

    @DisplayName("존재하지 않는 유저를 조회하면 빈 유저를 반환한다.")
    @Test
    void findByAccountEmpty() {
        Optional<User> user = InMemoryUserRepository.findByAccount("wrong");

        assertThat(user).isEmpty();
    }
}
