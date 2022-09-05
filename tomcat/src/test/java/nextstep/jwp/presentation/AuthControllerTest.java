package nextstep.jwp.presentation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.QueryParamNotFoundException;
import org.apache.coyote.http11.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AuthControllerTest {

    @DisplayName("존재하지 않는 유저일 경우 예외가 발생한다.")
    @Test
    void notExistUserException() {
        final AuthController authController = new AuthController();
        final String startLine = "GET /login?account=gu&password=password HTTP/1.1";

        assertThatThrownBy(() ->
                authController.run(startLine))
                .hasMessageContaining("존재하지 않는 유저입니다.")
                .isInstanceOf(UserNotFoundException.class);
    }

    @DisplayName("잘못된 param으로 UserRequest를 생성하여 예외가 발생한다.")
    @Test
    void queryParamNotFoundException() {
        final AuthController authController = new AuthController();
        final String startLine = "GET /login?account1=gu&password=password HTTP/1.1";

        assertThatThrownBy(() ->
                authController.run(startLine))
                .hasMessageContaining("잘못된 queryParam 입니다.")
                .isInstanceOf(QueryParamNotFoundException.class);
    }
}
