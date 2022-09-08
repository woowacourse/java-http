package nextstep.jwp.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.DuplicateAccountException;
import nextstep.jwp.exception.InvalidLoginFormatException;
import nextstep.jwp.exception.InvalidPasswordException;
import nextstep.jwp.exception.InvalidSignUpFormatException;
import nextstep.jwp.exception.MemberNotFoundException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.Test;

class AuthServiceTest {

    private final AuthService authService = AuthService.instance();

    @Test
    void 회원가입_테스트() {
        // given
        String requestBody = "account=test&password=password&email=test@woowahan.com";

        // when
        authService.signUp(requestBody);

        // then
        User actual = InMemoryUserRepository.findByAccount("test")
                .get();
        User expected = new User(2L, "test", "password", "test@woowahan.com");
        assertAll(
                () -> assertThat(actual).isEqualTo(expected)
        );
    }

    @Test
    void 이미_같은_계정으로_회원이_존재하면_예외를_반환한다() {
        // given
        String requestBody = "account=gugu&password=password&email=hkkang@woowahan.com";

        // when, then
        assertThatThrownBy(() -> authService.signUp(requestBody))
                .isExactlyInstanceOf(DuplicateAccountException.class);
    }

    @Test
    void 회원가입_형식이_올바르지_않으면_예외를_반환한다() {
        // given
        String requestBody = "query=invalid";

        // when, then
        assertThatThrownBy(() -> authService.signUp(requestBody))
                .isExactlyInstanceOf(InvalidSignUpFormatException.class);
    }

    @Test
    void 로그인_테스트() {
        // given
        String requestBody = "account=gugu&password=password";

        // when
        User loginUser = authService.login(requestBody);

        // then
        assertAll(
                () -> assertThat(loginUser.getAccount()).isEqualTo("gugu"),
                () -> assertThat(loginUser.getPassword()).isEqualTo("password")
        );
    }

    @Test
    void 계정_정보가_올바르지_않으면_예외를_반환한다() {
        // given
        String requestBody = "account=invalid&password=invalid";

        // when, then
        assertThatThrownBy(() -> authService.login(requestBody))
                .isExactlyInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 비밀번호가_올바르지_않으면_예외를_반환한다() {
        // given
        String requestBody = "account=gugu&password=invalid";

        // when, then
        assertThatThrownBy(() -> authService.login(requestBody))
                .isExactlyInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void 계정이나_비밀번호를_입력하지_않으면_예외를_반환한다() {
        // given
        String requestBody = "query=body";

        // when, then
        assertThatThrownBy(() -> authService.login(requestBody))
                .isExactlyInstanceOf(InvalidLoginFormatException.class);
    }
}
