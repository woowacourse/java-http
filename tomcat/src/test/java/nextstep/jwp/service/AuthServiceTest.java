package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.UnAuthenticatedException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("AuthService 은(는)")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class AuthServiceTest {

    private final AuthService authService = new AuthService();

    @Test
    void 아이디나_비밀번호가_null_이면_예외() {
        // when & then
        assertThatThrownBy(() ->
                authService.login(null, "password")
        ).isInstanceOf(UnAuthenticatedException.class);
        assertThatThrownBy(() ->
                authService.login("ss", null)
        ).isInstanceOf(UnAuthenticatedException.class);
    }

    @Test
    void 아이디가_없으면_예외() {
        // when & then
        assertThatThrownBy(() ->
                authService.login("wrong", "password")
        ).isInstanceOf(UnAuthenticatedException.class);
    }

    @Test
    void 비밀번호가_일치하지_않으면_예외() {
        // when & then
        assertThatThrownBy(() ->
                authService.login("gugu", "wrong")
        ).isInstanceOf(UnAuthenticatedException.class);
    }

    @Test
    void 로그인_성공() {
        // when
        User login = authService.login("gugu", "password");

        // then
        assertThat(login).isNotNull();
    }
}
