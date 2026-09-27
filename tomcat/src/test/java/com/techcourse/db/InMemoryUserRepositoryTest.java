package com.techcourse.db;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @Test
    void 저장한_회원을_계정으로_찾는다() {
        String account = "user-" + UUID.randomUUID();
        User user = new User(account, "password", "user@example.com");

        InMemoryUserRepository.save(user);

        assertThat(InMemoryUserRepository.findByAccount(account))
                .containsSame(user);
    }

    @Test
    void 등록되지_않은_계정은_빈_결과를_반환한다() {
        String account = "unknown-" + UUID.randomUUID();

        assertThat(InMemoryUserRepository.findByAccount(account))
                .isEmpty();
    }
}
