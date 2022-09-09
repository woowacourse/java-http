package nextstep.jwp.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    final InMemoryUserRepository userRepository = InMemoryUserRepository.getInstance();

    @Test
    @DisplayName("회원을 가입시킨다.")
    void save() {
        final User user = new User("test", "testPassword", "test@test.com");

        final User actual = userRepository.save(user);

        User expected = userRepository.findByAccount(actual.getAccount()).get();

        assertAll(
                () -> assertThat(actual.getEmail()).isEqualTo(expected.getEmail()),
                () -> assertThat(actual.getPassword()).isEqualTo(expected.getPassword()),
                () -> assertThat(actual.getAccount()).isEqualTo(expected.getAccount())
        );
    }
}
