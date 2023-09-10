package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.List;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class InMemoryUserRepositoryTest {

    @Test
    void 회원을_저장한다() {
        // given
        InMemoryUserRepository inMemoryUserRepository = InMemoryUserRepository.init();

        // when
        Long id = inMemoryUserRepository.save(new User("huchu", "huchu123", "huchu@naver.com"));

        // then
        assertThat(id).isNotNull();
    }

    @Test
    void 계정으로_회원을_조회한다() {
        // given
        InMemoryUserRepository inMemoryUserRepository = InMemoryUserRepository.init();
        User user = new User("huchu", "huchu123", "huchu@naver.com");
        inMemoryUserRepository.save(user);

        // when
        User foundUser = inMemoryUserRepository.findByAccount(user.getAccount()).get();

        // then
        assertThat(foundUser).isEqualTo(user);
    }

    @Test
    void 모든_회원을_조회한다() {
        // given
        InMemoryUserRepository inMemoryUserRepository = InMemoryUserRepository.init();

        // when
        List<User> users = inMemoryUserRepository.findAll();

        // then
        assertSoftly(softly -> {
            softly.assertThat(users).hasSize(1);
            softly.assertThat(users.get(0)).usingRecursiveComparison()
                    .isEqualTo(new User(1L, "gugu", "password", "hkkang@woowahan.com"));
        });
    }
}
