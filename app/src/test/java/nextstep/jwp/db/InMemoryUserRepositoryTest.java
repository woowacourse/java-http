package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository userRepository;

    @BeforeEach
    void setUp() {
        Map<String, User> database = new HashMap<>();
        userRepository = new InMemoryUserRepository(database, new AtomicLong(1));
    }

    @DisplayName("신규 User 저장")
    @Test
    void save() {
        // given
        User user = new User("account", "password", "email");

        // when
        userRepository.save(user);

        // then
        assertThat(userRepository.findByAccount(user.getAccount())).isPresent();
    }

    @DisplayName("신규 User 저장시 중복된 Account가 존재할 경우 예외처리")
    @Test
    void saveException() {
        // given
        String sameAccount = "account";
        User user1 = new User(sameAccount, "password1", "email1");
        User user2 = new User(sameAccount, "password2", "email2");

        // when
        userRepository.save(user1);

        // then
        assertThatThrownBy(() -> userRepository.save(user2))
            .isExactlyInstanceOf(DuplicateAccountException.class);
    }

    @DisplayName("Account를 이용한 User 조회")
    @Test
    void findByAccount() {
        // given
        User user = new User("account", "password", "email");

        // when
        userRepository.save(user);
        User foundUser = userRepository.findByAccount(user.getAccount())
            .orElseThrow(UnauthorizedException::new);

        // then
        assertThat(foundUser.getId()).isNotNull();
        assertThat(foundUser).usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(user);
    }
}
