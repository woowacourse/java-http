package nextstep.jwp.db;

import nextstep.jwp.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryUserRepositoryTest {

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.deleteAll();
        InMemoryUserRepository.save(new User("gugu", "password", "hkkang@woowahan.com"));
    }

    @Test
    void 사용자_저장시_id가_autoincrement_된다() {
        // given
        final User user = new User("corinne", "yoo77hyeon@gmail.com", "1234");
        final Long expected = 2L;

        // when
        InMemoryUserRepository.save(user);

        // then
        final User found = InMemoryUserRepository.findByAccount("corinne")
                .orElseThrow(IllegalArgumentException::new);

        assertThat(found.getId()).isEqualTo(expected);
    }
}
