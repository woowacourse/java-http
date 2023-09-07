package nextstep.jwp.db;

import nextstep.jwp.model.User;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @Test
    void save() {
        // given
        User user = new User("test","test","tes@test");
        User expect = user.putId(2L);

        // when
        InMemoryUserRepository.save(user);
        User actual = InMemoryUserRepository.findByAccount(user.getAccount()).orElseThrow();

        // then
        assertThat(actual).isEqualTo(expect);
    }

    @Test
    void findByAccount() {
        // given & when
        User expect = new User(1L,"gugu","password","hkkang@woowahan.com");
        User actual = InMemoryUserRepository.findByAccount("gugu").orElseThrow();

        // then
        assertThat(actual).isEqualTo(expect);
    }
}
