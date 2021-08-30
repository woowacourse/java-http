package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    private final User user = new User("test", "password", "aaron@email.com");

    @DisplayName("메모리 db에 저장한다.")
    @Test
    void save() {
        long savedCount = InMemoryUserRepository.savedSize();
        InMemoryUserRepository.save(user);

        assertThat(InMemoryUserRepository.savedSize() > savedCount).isTrue();
        assertThat(InMemoryUserRepository.findByAccount("test")).isPresent();
    }

    @DisplayName("account 로 저장된 유저를 조회한다.")
    @Test
    void findByAccount() {
        InMemoryUserRepository.save(user);

        assertThat(InMemoryUserRepository.findByAccount(user.getAccount()).get()).usingRecursiveComparison()
            .isEqualTo(user);
    }
}