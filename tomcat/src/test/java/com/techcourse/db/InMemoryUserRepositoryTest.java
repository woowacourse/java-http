package com.techcourse.db;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    private User user;

    @BeforeEach
    void init() {
        user = new User(1L, "kaki", "1234", "kaki@email.com");
        InMemoryUserRepository.save(user);

    }

    @DisplayName("account에 해당하는 회원이 없으면 Optional이 비어 있다.")
    @Test
    void findByAccountEmpty() {
        Optional<User> findUser = InMemoryUserRepository.findByAccount("abcd");

        boolean exists = findUser.isEmpty();

        assertThat(exists).isTrue();
    }

    @DisplayName("account에 해당하는 회원이 있으면 Optional에 데이터가 존재한다.")
    @Test
    void findByAccountPresent() {
        Optional<User> findUser = InMemoryUserRepository.findByAccount("kaki");

        boolean exists = findUser.isPresent();

        assertThat(exists).isTrue();
    }
}
