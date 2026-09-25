package com.techcourse.db;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @Test
    void 기본_회원을_가진다() {
        // when
        final var user = InMemoryUserRepository.findByAccount("gugu");

        // then
        assertThat(user).isPresent();
        assertThat(user.orElseThrow().checkPassword("password")).isTrue();
    }

    @Test
    void 회원을_저장한다() {
        // given
        final var user = new User("whale", "password", "whale@example.com");

        // when
        InMemoryUserRepository.save(user);

        // then
        assertThat(InMemoryUserRepository.findByAccount("whale"))
                .containsSame(user);
    }

    @Test
    void 계정으로_등록된_회원을_찾는다() {
        // when
        final var user = InMemoryUserRepository.findByAccount("gugu");

        // then
        assertThat(user).isPresent();
        assertThat(user.orElseThrow().getAccount()).isEqualTo("gugu");
    }

    @Test
    void 등록되지_않은_계정은_빈_결과로_반환한다() {
        // when
        final var user = InMemoryUserRepository.findByAccount("unknown");

        // then
        assertThat(user).isEmpty();
    }
}
