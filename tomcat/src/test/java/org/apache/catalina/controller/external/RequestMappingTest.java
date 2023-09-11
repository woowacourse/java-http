package org.apache.catalina.controller.external;

import nextstep.jwp.presentation.HomePageController;
import nextstep.jwp.presentation.LoginController;
import nextstep.jwp.presentation.LoginPageController;
import nextstep.jwp.presentation.RegisterPageController;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerMappingInfo;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.HttpFormTestUtils;
import org.apache.coyote.publisher.InputStreamRequestPublisher;
import org.apache.coyote.request.HttpRequest;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.coyote.request.HttpMethod.GET;
import static org.apache.coyote.request.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestMappingTest {

    private static final RequestMapping requestMapping = new RequestMapping()
            .putController(ControllerMappingInfo.of(GET, false, "/"), new HomePageController())
            .putController(ControllerMappingInfo.of(GET, false, "/login"), new LoginPageController())
            .putController(ControllerMappingInfo.of(GET, true, "/login"), new LoginController())
            .putController(ControllerMappingInfo.of(GET, false, "/register"), new RegisterPageController())
            .putController(ControllerMappingInfo.of(POST, false, "/register"), new RegisterPageController());

    @Test
    void 외부의_HomePageController를_가져올_수_있다() {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .contentLength("0").enter()
                .accept("text/html;charset=utf-8").enter()
                .enter()
                .build();

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(UTF_8));
        final HttpRequest request = InputStreamRequestPublisher.read(inputStream).toHttpRequest();

        // when
        final Controller foundController = requestMapping.getController(request);

        // then
        assertThat(foundController).isInstanceOf(HomePageController.class);
    }

    @Test
    void 외부의_LoginPageController를_가져올_수_있다() {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/login").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .contentLength("0").enter()
                .accept("text/html;charset=utf-8").enter()
                .enter()
                .build();

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(UTF_8));
        final HttpRequest request = InputStreamRequestPublisher.read(inputStream).toHttpRequest();

        // when
        final Controller foundController = requestMapping.getController(request);

        // then
        assertThat(foundController).isInstanceOf(LoginPageController.class);
    }

    @Test
    void 외부의_LoginController를_가져올_수_있다() {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/login?name=hyena&password=test").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .contentLength("0").enter()
                .accept("text/html;charset=utf-8").enter()
                .enter()
                .build();

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(UTF_8));
        final HttpRequest request = InputStreamRequestPublisher.read(inputStream).toHttpRequest();

        // when
        final Controller foundController = requestMapping.getController(request);

        // then
        assertThat(foundController).isInstanceOf(LoginController.class);
    }

    @Test
    void 외부의_GET_RegisterPageController를_가져올_수_있다() {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .GET().requestUri("/register").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .contentLength("0").enter()
                .accept("text/html;charset=utf-8").enter()
                .enter()
                .build();

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(UTF_8));
        final HttpRequest request = InputStreamRequestPublisher.read(inputStream).toHttpRequest();

        // when
        final Controller foundController = requestMapping.getController(request);

        // then
        assertThat(foundController).isInstanceOf(RegisterPageController.class);
    }

    @Test
    void 외부의_POST_RegisterPageController를_가져올_수_있다() {
        // given
        final String httpRequest = HttpFormTestUtils.builder()
                .POST().requestUri("/register").http11().enter()
                .host("localhost:8080").enter()
                .connection("keep-alive").enter()
                .contentLength("0").enter()
                .accept("text/html;charset=utf-8").enter()
                .enter()
                .build();

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes(UTF_8));
        final HttpRequest request = InputStreamRequestPublisher.read(inputStream).toHttpRequest();

        // when
        final Controller foundController = requestMapping.getController(request);

        // then
        assertThat(foundController).isInstanceOf(RegisterPageController.class);
    }
}
