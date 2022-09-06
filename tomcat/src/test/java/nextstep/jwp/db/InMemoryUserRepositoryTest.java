package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class InMemoryUserRepositoryTest {

    @Test
    void save() {
        User user = new User("account", "password", "email@email.com");
        InMemoryUserRepository.save(user);

        Optional<User> actual = InMemoryUserRepository.findByAccount("account");

        assertThat(actual).isNotEmpty();
    }

    @Test
    void findByAccount() {
        Optional<User> user = InMemoryUserRepository.findByAccount("gugu");

        assertThat(user).isNotEmpty();
    }

    @ParameterizedTest
    @CsvSource(value = {"gugu,false", "newAccount,true"})
    void isRegistrable(final String account, final boolean registrable) {
        boolean actual = InMemoryUserRepository.isRegistrable(account);

        assertThat(actual).isEqualTo(registrable);
    }
}
