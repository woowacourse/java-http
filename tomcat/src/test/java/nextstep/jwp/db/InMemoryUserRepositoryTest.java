package nextstep.jwp.db;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class InMemoryUserRepositoryTest {

    @Test
    void account로_유저를_검색했을때_유저가_존재하면_유저를_반환한다() {
        // given
        final String account = "bebe";
        final User expected = new User(account, "password", "bebe@gmail.com");
        InMemoryUserRepository.save(expected);

        // when
        final Optional<User> actual = InMemoryUserRepository.findByAccount(account);

        // then
        assertThat(actual).contains(expected);
    }

    @Test
    void account로_유저를_검색했을때_유저가_존재하지_않으면_빈_Optional을_반환한다() {
        // given, when
        final String notExistAccount = "ditoo";
        final Optional<User> actual = InMemoryUserRepository.findByAccount(notExistAccount);

        // then
        assertThat(actual).isNotPresent();
    }
}
