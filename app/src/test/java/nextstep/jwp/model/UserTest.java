package nextstep.jwp.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.db.InMemoryUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class UserTest {

    @DisplayName("User를 생성한다.")
    @Test
    void create() {
        User user = new User(100L, "aaron", "password", "aaron@email.com");

        assertThat(user.getAccount()).isEqualTo("aaron");
        assertThat(user.checkPassword("password")).isTrue();
        assertThat(user.getEmail()).isEqualTo("aaron@email.com");
    }

    @DisplayName("User를 생성할때 아이디를 자동으로 생성한다.")
    @Test
    void autoCreateUserID() {
        long idCount = InMemoryUserRepository.savedSize();
        User user1 = new User("user1", "password", "email");
        User user2 = new User("user2", "password", "email");

        assertThat(user1.getId()).isEqualTo(idCount + 1);
        assertThat(user2.getId()).isEqualTo(idCount + 2);
    }

    @DisplayName("User 생성시 누락된 정보가 있으면 예외를 반환한다.")
    @ParameterizedTest
    @CsvSource({"aaron, password, ' '", "aaron, '', email", "'  ', password, email"})
    void creationFail(String account, String password, String email) {
        assertThatThrownBy(() -> new User(account, password, email))
            .isInstanceOf(IllegalArgumentException.class);
    }
}