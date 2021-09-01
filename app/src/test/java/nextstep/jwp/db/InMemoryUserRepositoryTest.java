package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.DBNotFoundException;
import nextstep.jwp.exception.PasswordMismatchException;
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

    @DisplayName("유저 조회를 실패한다.")
    @Test
    void accountNotFoundException() {
        //then
        assertThatThrownBy(() -> InMemoryUserRepository.findByAccount("wilder").orElseThrow(DBNotFoundException::new))
            .isInstanceOf(DBNotFoundException.class);
    }

    @DisplayName("유저 조회를 실패한다.")
    @Test
    void passwordMismatchException() {
        //given
        User gugu = InMemoryUserRepository.findByAccount("gugu").orElseThrow(DBNotFoundException::new);

        //then
        assertThatThrownBy(() -> {
            if (!gugu.checkPassword("123")) {
                throw new PasswordMismatchException();
            }
        }).isInstanceOf(PasswordMismatchException.class);
    }
}
