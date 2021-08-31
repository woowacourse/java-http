package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.exception.DBNotFoundException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryUserRepositoryTest {

    @DisplayName("유저를 조회한다.")
    @Test
    void find() {
        //when
        User gugu = InMemoryUserRepository.findByAccount("gugu").orElseThrow(DBNotFoundException::new);

        //then
        assertThat(gugu.getAccount()).isEqualTo("gugu");
    }

    @DisplayName("유저를 저장한다.")
    @Test
    void save() {
        //given
        User user = new User(2, "wilder", "123", "wilder@wilder.com");
        InMemoryUserRepository.save(user);

        //when
        User wilder = InMemoryUserRepository.findByAccount("wilder").orElseThrow(DBNotFoundException::new);

        //then
        assertThat(wilder.getAccount()).isEqualTo(user.getAccount());
    }
}
