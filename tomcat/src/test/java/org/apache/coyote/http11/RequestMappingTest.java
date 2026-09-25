package org.apache.coyote.http11;

import com.techcourse.Application;
import com.techcourse.web.HomeController;
import com.techcourse.web.LoginController;
import com.techcourse.web.RegisterController;
import com.techcourse.web.RequestMapping;
import com.techcourse.web.Route;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingTest {

    @ParameterizedTest
    @MethodSource("registeredRoutes")
    void 등록한_경로를_GET으로_요청하면_각_경로에_등록한_컨트롤러를_반환한다(
            Route route, Class<? extends Controller> controllerType
    ) throws IOException {
        // given
        RequestMapping mapping = Application.createRequestMapping();

        // when
        Optional<Controller> found = mapping.getController(request("GET", route.getPath()));

        // then
        assertThat(found).hasValueSatisfying(controller -> assertThat(controller).isInstanceOf(controllerType));
    }

    @Test
    void 로그인_경로의_GET과_POST는_동일한_컨트롤러를_반환한다() throws IOException {
        // given
        RequestMapping mapping = Application.createRequestMapping();

        // when
        Optional<Controller> get = mapping.getController(request("GET", Route.LOGIN.getPath()));
        Optional<Controller> post = mapping.getController(request("POST", Route.LOGIN.getPath()));

        // then
        assertThat(get.orElseThrow()).isSameAs(post.orElseThrow());
    }

    @Test
    void 등록되지_않은_경로로_요청하면_컨트롤러를_반환하지_않는다() throws IOException {
        // given
        RequestMapping mapping = Application.createRequestMapping();

        // when
        Optional<Controller> found = mapping.getController(request("GET", "/missing"));

        // then
        assertThat(found).isEmpty();
    }

    private static Stream<Arguments> registeredRoutes() {
        return Stream.of(
                Arguments.of(Route.HOME, HomeController.class),
                Arguments.of(Route.LOGIN, LoginController.class),
                Arguments.of(Route.REGISTER, RegisterController.class)
        );
    }

    private HttpRequest request(String method, String path) throws IOException {
        String raw = method + " " + path + " HTTP/1.1\r\n\r\n";
        ByteArrayInputStream input = new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8));
        return new HttpRequestParser(input).parse();
    }
}
