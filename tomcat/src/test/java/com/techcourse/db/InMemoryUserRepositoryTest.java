package com.techcourse.db;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.reset();
    }

    @DisplayName("유저를 저장소에 저장한다.")
    @Test
    void saveTest() {
        User user = new User("jazz", "password", "jazz@woowa.net");
        InMemoryUserRepository.save(user);

        User savedUser = InMemoryUserRepository.findByAccount(user.getAccount()).get();

        assertThat(savedUser.getAccount()).isEqualTo(user.getAccount());
    }

    @DisplayName("reset 메서드를 호출하면 저장소를 초기 상태로 되돌린다.")
    @Test
    void resetTest() {
        User user = new User("jazz", "password", "jazz@woowa.net");
        InMemoryUserRepository.save(user);

        assertThat(InMemoryUserRepository.findByAccount(user.getAccount())).isPresent();

        InMemoryUserRepository.reset();

        assertThat(InMemoryUserRepository.findByAccount(user.getAccount())).isEmpty();
    }


}
