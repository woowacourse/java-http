package nextstep.jwp.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.InvalidLoginFormatException;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.MemberNotFoundException;
import org.junit.jupiter.api.Test;

class LoginServiceTest {

    private final LoginService loginService = LoginService.instance();

    @Test
    void 로그인_테스트() {
        // given
        String requestBody = "account=gugu&password=password";

        // when
        String location = loginService.login(requestBody);

        // then
        assertThat(location).isEqualTo("/index.html");
    }

    @Test
    void 계정_정보가_올바르지_않으면_예외를_반환한다() {
        // given
        String requestBody = "account=invalid&password=invalid";

        // when, then
        assertThatThrownBy(() -> loginService.login(requestBody))
                .isExactlyInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 비밀번호가_올바르지_않으면_예외를_반환한다() {
        // given
        String requestBody = "account=gugu&password=invalid";

        // when, then
        assertThatThrownBy(() -> loginService.login(requestBody))
                .isExactlyInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void 계정이나_비밀번호를_입력하지_않으면_예외를_반환한다() {
        // given
        String requestBody = "query=body";

        // when, then
        assertThatThrownBy(() -> loginService.login(requestBody))
                .isExactlyInstanceOf(InvalidLoginFormatException.class);
    }
}
