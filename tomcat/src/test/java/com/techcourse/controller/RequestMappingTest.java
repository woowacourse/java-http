package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("요청 매핑")
class RequestMappingTest {

    private final Controller loginController = mock(Controller.class);
    private final Controller defaultController = mock(Controller.class);
    private final RequestMapping requestMapping = new RequestMapping(
            Map.of("/login", loginController),
            defaultController);

    @Test
    @DisplayName("등록된 경로의 컨트롤러를 반환한다")
    void returnsControllerMappedToPath() throws Exception {
        // given
        final var request = request("/login");

        // when
        final var controller = requestMapping.getController(request);

        // then
        assertThat(controller).isSameAs(loginController);
    }

    @Test
    @DisplayName("등록되지 않은 경로에는 기본 컨트롤러를 반환한다")
    void returnsDefaultControllerForUnmappedPath() throws Exception {
        // given
        final var request = request("/index.html");

        // when
        final var controller = requestMapping.getController(request);

        // then
        assertThat(controller).isSameAs(defaultController);
    }

    private HttpRequest request(final String path) throws Exception {
        final var rawRequest = String.join("\r\n",
                "GET " + path + " HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        return HttpRequest.readFrom(new BufferedReader(new StringReader(rawRequest))).orElseThrow();
    }
}
