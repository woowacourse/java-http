package com.techcourse.web;

import com.techcourse.Application;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RequestMappingTest {

    @ParameterizedTest
    @MethodSource("registeredRoutes")
    void 등록한_경로를_GET으로_요청하면_각_경로에_등록한_컨트롤러를_반환한다(
            Route route, Class<? extends Controller> controllerType
    ) {
        // given
        RequestMapping mapping = Application.createRequestMapping();
        HttpRequest request = request(HttpMethod.GET, route.getPath());

        // when
        Optional<Controller> found = mapping.getController(request);

        // then
        assertThat(found).hasValueSatisfying(controller -> assertThat(controller).isInstanceOf(controllerType));
    }

    @Test
    void 로그인_경로의_GET과_POST는_동일한_컨트롤러를_반환한다() {
        // given
        RequestMapping mapping = Application.createRequestMapping();
        HttpRequest getRequest = request(HttpMethod.GET, Route.LOGIN.getPath());
        HttpRequest postRequest = request(HttpMethod.POST, Route.LOGIN.getPath());

        // when
        Optional<Controller> get = mapping.getController(getRequest);
        Optional<Controller> post = mapping.getController(postRequest);

        // then
        assertThat(get.orElseThrow()).isSameAs(post.orElseThrow());
    }

    @Test
    void 등록되지_않은_경로로_요청하면_컨트롤러를_반환하지_않는다() {
        // given
        RequestMapping mapping = Application.createRequestMapping();
        HttpRequest request = request(HttpMethod.GET, "/missing");

        // when
        Optional<Controller> found = mapping.getController(request);

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

    private HttpRequest request(HttpMethod method, String path) {
        HttpRequest request = mock(HttpRequest.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getPath()).thenReturn(path);
        return request;
    }
}
