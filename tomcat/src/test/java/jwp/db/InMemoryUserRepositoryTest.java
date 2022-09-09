package jwp.db;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;

public class InMemoryUserRepositoryTest {

    @Test
    @DisplayName("계정이 존재하면 User를 찾아 반환한다.")
    void findUserByAccount() {
        // given
        User user = new User("panda", "password", "panda@gmail.com");
        InMemoryUserRepository.save(user);

        // when
        Optional<User> foundUser = InMemoryUserRepository.findByAccount("panda");

        // then
        assertThat(foundUser.get()).isEqualTo(user);
    }

    @Test
    @DisplayName("계정이 존재하지 않으면 빈 값을 반환한다.")
    void notExistAccount() {
        // when
        Optional<User> foundUser = InMemoryUserRepository.findByAccount("brie");

        // then
        assertThat(foundUser).isEmpty();
    }
}
