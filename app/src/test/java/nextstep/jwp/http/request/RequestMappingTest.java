package nextstep.jwp.http.request;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.controller.RegisterController;
import nextstep.jwp.controller.StaticResourceController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("RequestMapping 테스트")
class RequestMappingTest {

    private final RegisterController registerController = mock(RegisterController.class);
    private final LoginController loginController = mock(LoginController.class);
    private final StaticResourceController staticResourceController = mock(StaticResourceController.class);

    private RequestMapping requestMapping;

    @BeforeEach
    void setUp() {
        final Map<String, Controller> controllers = new ConcurrentHashMap<>();
        controllers.put("/register", registerController);
        controllers.put("/login", loginController);

        requestMapping = new RequestMapping(controllers, staticResourceController);
    }

    @DisplayName("request uri가 /register일 때 RegisterController 반환 테스트")
    @Test
    void getRegisterController() {
        //given
        final String requestUri = "/register";

        //when
        final Controller controller = requestMapping.getController(requestUri);

        //then
        assertThat(controller).isInstanceOf(RegisterController.class);
    }

    @DisplayName("request uri가 /login일 때 LoginController 반환 테스트")
    @Test
    void getLoginController() {
        //given
        final String requestUri = "/login";

        //when
        final Controller controller = requestMapping.getController(requestUri);

        //then
        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @DisplayName("request uri가 static resource 일 때 StaticResourceController 반환 테스트")
    @Test
    void getStaticResourceController() {
        //given
        final String requestUri = "/index.html";

        //when
        final Controller controller = requestMapping.getController(requestUri);

        //then
        assertThat(controller).isInstanceOf(StaticResourceController.class);
    }
}