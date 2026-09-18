package com.techcourse.db;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryUserRepositoryTest {

    @Test
    void findAllIncludesPreRegisteredUser() {
        assertThat(InMemoryUserRepository.findAll())
                .extracting(user -> user.getAccount())
                .contains("gugu");
    }
}
