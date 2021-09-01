package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RequestMappingTest")
class RequestMappingTest {

    private static final RequestMapping requestMapping = new RequestMapping();

    @Test
    @DisplayName("로그인 컨트롤러 반환 테스트")
    void loginController() {
        // given
        Request request = createRequest("/login");

        // when
        Controller controller = requestMapping.getController(request);

        // then
        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @Test
    @DisplayName("레지스터 컨트롤러 반환 테스트")
    void registerController() {
        // given
        Request request = createRequest("/register");

        // when
        Controller controller = requestMapping.getController(request);

        // then
        assertThat(controller).isInstanceOf(RegisterController.class);
    }

    @Test
    @DisplayName("로그인 컨트롤러 반환 테스트")
    void methodNotAllowed() {
        // given
        Request request = createRequest("/error");

        // when
        assertThatThrownBy(() -> requestMapping.getController(request))
        .isInstanceOf(MethodNotAllowedException.class);

    }

    private Request createRequest(String uri) {
        return new Request.Builder()
            .method(HttpMethod.GET)
            .uri(uri)
            .header(new HashMap<>())
            .body(new HashMap<>())
            .httpVersion("HTTP/1.1")
            .build();
    }
}