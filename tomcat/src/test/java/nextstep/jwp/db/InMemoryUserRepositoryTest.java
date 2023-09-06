package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Optional;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class InMemoryUserRepositoryTest {

    @Test
    void save_메서드는_전달한_User를_저장한다() {
        final User user = new User(5L, "asdf", "asdf", "asdf@asdf.com");

        assertDoesNotThrow(() -> InMemoryUserRepository.save(user));
    }

    @Test
    void findByAccount_메서드는_존재하는_account를_전달하면_해당_User를_Optional로_감싸_반환한다() {
        final String existsAccount = "gugu";

        final Optional<User> actual = InMemoryUserRepository.findByAccount(existsAccount);

        assertThat(actual).isPresent();
    }

    @Test
    void findByAccount_메서드는_존재하지_않는_account를_전달하면_빈_Optional을_반환한다() {
        final String notExistsAccount = "asdfasdfasdfasdfa";

        final Optional<User> actual = InMemoryUserRepository.findByAccount(notExistsAccount);

        assertThat(actual).isEmpty();
    }

    @Test
    void existsByAccount_메서드는_존재하는_account를_전달하면_true를_반환한다() {
        final String existsAccount = "gugu";

        final boolean actual = InMemoryUserRepository.existsByAccount(existsAccount);

        assertThat(actual).isTrue();
    }

    @Test
    void existsByAccount_메서드는_존재하지_않는_account를_전달하면_false를_반환한다() {
        final String notExistsAccount = "asdf";

        final boolean actual = InMemoryUserRepository.existsByAccount(notExistsAccount);

        assertThat(actual).isTrue();
    }
}
