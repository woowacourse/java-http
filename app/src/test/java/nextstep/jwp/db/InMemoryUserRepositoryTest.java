package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
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
        userRepository = new InMemoryUserRepository(database, 1L);
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
