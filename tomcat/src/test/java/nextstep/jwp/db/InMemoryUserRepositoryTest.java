package nextstep.jwp.db;

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
    void 모든_회원을_조회한다() {
        // when
        List<User> users = InMemoryUserRepository.findAll();

        // then
        assertSoftly(softly -> {
            softly.assertThat(users).hasSize(1);
            softly.assertThat(users.get(0)).usingRecursiveComparison()
                    .isEqualTo(new User(1L, "gugu", "password", "hkkang@woowahan.com"));
        });
    }
}
