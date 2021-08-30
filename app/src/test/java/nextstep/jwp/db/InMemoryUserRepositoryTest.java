package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {
    @Test
    @DisplayName("User 저장")
    void save() {
        User inputUser = new User(null, "wannte", "password", "mail@mail.com");

        User savedUser = InMemoryUserRepository.save(inputUser);

        assertThat(savedUser.getId()).isNotNull();
    }

    @Test
    @DisplayName("User 저장 시 동일 Account 에러 발생")
    void saveDouble() {
        User inputUser = new User(null, "wannte", "password", "mail@mail.com");

        InMemoryUserRepository.save(inputUser);

        assertThatThrownBy(() -> InMemoryUserRepository.save(inputUser)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("User 검색 - 성공")
    void findByAccount() {
        String account = "wannte";
        User user = new User(null, account, "password", "mail@mail.com");
        InMemoryUserRepository.save(user);

        Optional<User> foundUser = InMemoryUserRepository.findByAccount(account);
        assertThat(foundUser.isPresent()).isTrue();
    }

    @Test
    @DisplayName("User 검색 - 존재하지 않는 Account")
    void findByNotExistingAccount() {
        String notExistingAccount = "notExisting";

        Optional<User> foundUser = InMemoryUserRepository.findByAccount(notExistingAccount);
        assertThat(foundUser.isEmpty()).isTrue();
    }

    @AfterEach
    void tearDown() {
        InMemoryUserRepository.clear();
    }
}
