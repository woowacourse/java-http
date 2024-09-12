package com.techcourse.db;

import com.techcourse.model.domain.User;
import com.techcourse.model.dto.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryUserRepositoryTest {

    @Test
    @DisplayName("유저를 저장한다.")
    void save() {
        UserInfo userInfo = new UserInfo("account", "password", "test@email.com");

        User user = InMemoryUserRepository.save(userInfo);

        assertThat(user.getAccount()).isEqualTo("account");
    }

    @Test
    @DisplayName("계정명으로 유저를 조회한다.")
    void findByAccount() {
        String account = "gugu";

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        assertThat(user.get().getAccount()).isEqualTo(account);
    }
}
