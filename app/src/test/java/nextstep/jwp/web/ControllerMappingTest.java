package nextstep.jwp.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.NoMatchingHandlerException;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.RegisterController;
import nextstep.jwp.web.http.response.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("적합한 Controller를 매핑하는 로직을 테스트한다.")
class ControllerMappingTest {

    @DisplayName("/login 에 맞는 컨트롤러를 반환한다. - 성공")
    @Test
    void getController_login_Success() {
        // given
        String url = "/login";

        // when
        // then
        assertThat(ControllerMapping.getController(url).getClass())
            .isEqualTo(LoginController.class);
    }

    @DisplayName("/register 에 맞는 컨트롤러를 반환한다. - 성공")
    @Test
    void getController_register_Success() {
        // given
        String url = "/register";

        // when
        // then
        assertThat(ControllerMapping.getController(url).getClass())
            .isEqualTo(RegisterController.class);
    }

    @DisplayName("url에 매핑되는 컨트롤러가 없을 경우 404 예외가 발생한다. - 실패")
    @Test
    void getController_noMatchingController_404ExceptionThrown() {
        // given
        String url = "/invalid";

        // when
        // then
        assertThatThrownBy(() -> ControllerMapping.getController(url))
            .isInstanceOf(NoMatchingHandlerException.class)
            .hasFieldOrPropertyWithValue("statusCode", StatusCode.NOT_FOUND);
    }
}
