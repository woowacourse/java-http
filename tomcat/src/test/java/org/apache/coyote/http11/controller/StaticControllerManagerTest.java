package org.apache.coyote.http11.controller;

import common.http.HttpMethod;
import common.http.Request;
import nextstep.jwp.controller.LoginController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StaticControllerManagerTest {

    @Test
    void 컨트롤러를_추가할_시_예외를_반환한다() {
        // given
        StaticControllerManager staticControllerManager = new StaticControllerManager();
        LoginController loginController = new LoginController();

        // expect
        Assertions.assertThatThrownBy(() -> staticControllerManager.add("/login", loginController))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("컨트롤러를 추가할 수 없습니다.");
    }

    @Test
    void 정적파일을_제공하는_컨트롤러를_찾아서_service를_실행한다() {
        // given
        StaticControllerManager staticControllerManager = new StaticControllerManager();
        Request request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getVersionOfTheProtocol()).thenReturn("HTTP/1.1");
        when(request.getPath()).thenReturn("/login.html");
        when(request.getHttpMethod()).thenReturn(HttpMethod.GET);

        // when
        staticControllerManager.service(request, response);

        // then
        assertThat(response.toString()).contains("HTTP/1.1 200 OK");
    }
}
