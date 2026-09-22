package com.techcourse.db;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.util.UUID;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @Test
    void savesOnlyTheFirstUserForAnAccount() {
        final String account = "save-" + UUID.randomUUID();
        final User original = new User(account, "original", "original@example.com");
        final User duplicate = new User(account, "replacement", "replacement@example.com");

        assertThat(InMemoryUserRepository.save(original)).isTrue();
        assertThat(InMemoryUserRepository.save(duplicate)).isFalse();
        assertThat(InMemoryUserRepository.findByAccount(account)).containsSame(original);
    }

    @Test
    void concurrentRegistrationsHaveExactlyOneWinner() throws Exception {
        final String account = "concurrent-" + UUID.randomUUID();
        final User first = new User(account, "first", "first@example.com");
        final User second = new User(account, "second", "second@example.com");
        final var barrier = new CyclicBarrier(2);

        try (var executor = Executors.newFixedThreadPool(2)) {
            final var firstResult = executor.submit(() -> {
                barrier.await(5, TimeUnit.SECONDS);
                return InMemoryUserRepository.save(first);
            });
            final var secondResult = executor.submit(() -> {
                barrier.await(5, TimeUnit.SECONDS);
                return InMemoryUserRepository.save(second);
            });
            final boolean firstSaved = firstResult.get(5, TimeUnit.SECONDS);
            final boolean secondSaved = secondResult.get(5, TimeUnit.SECONDS);

            assertThat(firstSaved).isNotEqualTo(secondSaved);
            assertThat(InMemoryUserRepository.findByAccount(account))
                    .containsSame(firstSaved ? first : second);
        }
    }
}
