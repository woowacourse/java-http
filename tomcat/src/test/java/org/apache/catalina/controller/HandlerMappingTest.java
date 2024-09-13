package org.apache.catalina.controller;

import static org.apache.coyote.http11.Http11Processor.HTTP_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.controller.LogInController;
import org.apache.catalina.exception.ControllerNotMatchedException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HandlerMappingTest {

    @Test
    @DisplayName("요청에 해당하는 컨트롤러를 반환한다.")
    void getController() {
        // given
        final var handlerMapping = new HandlerMapping();
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, "/login", null, HTTP_VERSION),
                null, null
        );

        // when
        final var controller = handlerMapping.getController(request);

        // then
        assertThat(controller).isInstanceOf(LogInController.class);
    }

    @Test
    @DisplayName("요청에 해당하는 컨트롤러가 없으면 예외를 발생시킨다.")
    void getControllerWhenControllerNotFound() {
        // given
        final var handlerMapping = new HandlerMapping();
        HttpRequest request = new HttpRequest(
                new HttpRequestLine(HttpMethod.POST, "/logout", null, HTTP_VERSION),
                null, null
        );

        // when, then
        assertThatThrownBy(() -> handlerMapping.getController(request))
                .isInstanceOf(ControllerNotMatchedException.class);
    }
}
