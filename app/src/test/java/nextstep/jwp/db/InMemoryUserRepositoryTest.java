package nextstep.jwp.db;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InMemoryUserRepository 테스트")
class InMemoryUserRepositoryTest {

    private final InMemoryUserRepository inMemoryUserRepository = new InMemoryUserRepository();

    @DisplayName("새로운 User를 저장할 때 auto_increment 인덱스를 적용한다.")
    @Test
    void saveWithAutoIncrementIndex() {
        //given
        final User user1 = new User("인비1", "1234", "인비1@email.com");
        final User user2 = new User("인비2", "1234", "인비2@email.com");

        //when
        final User savedUser1 = inMemoryUserRepository.save(user1);
        final User savedUser2 = inMemoryUserRepository.save(user2);

        //then
        assertThat(savedUser1.getId()).isEqualTo(2L);
        assertThat(savedUser2.getId()).isEqualTo(3L);
    }
}