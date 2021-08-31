package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.web.controller.Controller;
import nextstep.jwp.web.controller.HomeController;
import nextstep.jwp.web.controller.LoginController;
import nextstep.jwp.web.controller.RegisterController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RequestMapperTest {

    public static Stream<Arguments> controllerMapping() {
        return Stream.of(
                Arguments.of("GET", "/", HomeController.class),
                Arguments.of("GET", "/login", LoginController.class),
                Arguments.of("GET", "/register", RegisterController.class)
        );
    }

    @ParameterizedTest
    @MethodSource("controllerMapping")
    void findController(String method, String uri, Class<? extends Controller> controllerClass) {
        HttpRequest httpRequest = new HttpRequest(method, uri);

        Controller controller = RequestMapper.findController(httpRequest);

        assertThat(controller).isInstanceOf(controllerClass);
    }

    @Test
    void findNotExistingController() {
        HttpRequest httpRequest = new HttpRequest("GET", "/notExistUri");

        assertThatThrownBy(
                () -> RequestMapper.findController(httpRequest)
        ).isInstanceOf(NotFoundException.class);
    }
}
