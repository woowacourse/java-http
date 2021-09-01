package nextstep.jwp.web.handler;

import static nextstep.jwp.web.http.request.HttpMethod.GET;
import static nextstep.jwp.web.http.request.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import nextstep.jwp.application.controller.LoginController;
import nextstep.jwp.application.controller.RegisterController;
import nextstep.jwp.application.controller.WelcomeController;
import nextstep.jwp.web.http.request.MethodUrl;
import nextstep.jwp.web.mvc.controller.Controller;
import nextstep.jwp.web.mvc.controller.NotFoundController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RequestMappingTest {

    @DisplayName("요청과 컨트롤러 맵핑 기능")
    @ParameterizedTest
    @MethodSource
    void requestMappingTest(MethodUrl methodUrl, Class<? super Controller> controllerType) {
        //given
        RequestMapping requestMapping = new RequestMapping();
        //when
        Controller controller = requestMapping.getController(methodUrl);
        //then
        assertThat(controller).isInstanceOf(controllerType);
    }

    private static Stream<Arguments> requestMappingTest() {
        return Stream.of(
            Arguments.of(
                new MethodUrl(GET, "/index"), WelcomeController.class
            ),
            Arguments.of(
                new MethodUrl(GET, "/login"), LoginController.class
            ),
            Arguments.of(
                new MethodUrl(POST, "/login"), LoginController.class
            ),
            Arguments.of(
                new MethodUrl(GET, "/register"), RegisterController.class
            ),
            Arguments.of(
                new MethodUrl(POST, "/register"), RegisterController.class
            ),
            Arguments.of(
                new MethodUrl(GET, "/neverEverApi"), NotFoundController.class
            )
        );
    }
}