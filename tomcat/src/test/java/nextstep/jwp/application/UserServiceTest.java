package nextstep.jwp.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import nextstep.jwp.application.exception.AlreadyExistsAccountException;
import nextstep.jwp.model.User;
import nextstep.handler.exception.LoginFailureException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserServiceTest {

    UserService userService = new UserService();

    @Test
    void login_메서드는_주어진_account와_password가_존재한다면_해당_User를_반환한다() {
        final String validAccount = "gugu";
        final String validPassword = "password";

        final User actual = userService.login(validAccount, validPassword);

        assertThat(actual).isNotNull();
    }

    @Test
    void login_메서드는_존재하지_않는_account인_경우_예외가_발생한다() {
        final String invalidAccount = "asdf";
        final String invalidPassword = "asdfasdfasdf";

        assertThatThrownBy(() -> userService.login(invalidAccount, invalidPassword))
                .isInstanceOf(LoginFailureException.class)
                .hasMessageContaining("로그인에 실패했습니다.");
    }

    @Test
    void login_메서드는_account의_비밀번호가_틀린_경우_예외가_발생한다() {
        final String validAccount = "gugu";
        final String invalidPassword = "asdfasdfasdf";

        assertThatThrownBy(() -> userService.login(validAccount, invalidPassword))
                .isInstanceOf(LoginFailureException.class)
                .hasMessageContaining("로그인에 실패했습니다.");
    }

    @Test
    void register_메서드는_필요한_데이터를_저장하면_해당_데이터를_기반으로_User를_추가한다() {
        assertDoesNotThrow(() -> userService.register("account", "password", "email"));
    }

    @Test
    void register_메서드는_이미_존재하는_account를_전달하면_예외가_발생한다() {
        assertThatThrownBy(() -> userService.register("gugu", "password", "email"))
                .isInstanceOf(AlreadyExistsAccountException.class)
                .hasMessageContaining("이미 존재하는 아이디입니다.");
    }
}
