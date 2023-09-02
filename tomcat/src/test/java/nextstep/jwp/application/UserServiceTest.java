package nextstep.jwp.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("UserService 테스트")
class UserServiceTest {

    private final UserService userService = new UserService();

    @Test
    void 로그인_성공_여부_테스트() {
        // given
        final String existAccount = "gugu";
        final String password = "password";

        // when
        final boolean result = userService.validateLogin(existAccount, password);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 계정으로_조회시_존재하지_않으면_예외_발생() {
        // given
        final String notExistAccount = "notExistAccount";

        // when & then
        Assertions.assertThatThrownBy(() -> userService.getUserByAccount(notExistAccount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }
}
