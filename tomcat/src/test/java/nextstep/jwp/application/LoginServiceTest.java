package nextstep.jwp.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import nextstep.jwp.exception.NotFoundUserException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class LoginServiceTest {

    private final LoginService loginService = new LoginService();

    @Test
    void 유저를_찾는다() {
        assertDoesNotThrow(() -> loginService.findUser("gugu"));
    }

    @Test
    void 유저가_존재하지않는데_유저를_찾으면_예외가_발생한다() {
        assertThatThrownBy(() -> loginService.findUser("ori"))
                .isInstanceOf(NotFoundUserException.class);
    }

    @ParameterizedTest
    @CsvSource(value = {"gugu,password,hkkang@woowahan.com,true", "ori,password,jinyoungchoi95@gmail.com,false"})
    void 유저가_존재하는지_확인한다(final String account, final String password, final String email, final boolean expected) {
        User user = new User(account, password, email);
        boolean actual = loginService.existsUser(user);

        assertThat(actual).isEqualTo(expected);
    }
}
