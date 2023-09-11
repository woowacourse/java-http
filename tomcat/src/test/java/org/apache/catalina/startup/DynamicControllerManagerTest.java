package org.apache.catalina.startup;

import common.http.HttpMethod;
import common.http.Request;
import common.http.Session;
import nextstep.jwp.controller.LoginController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DynamicControllerManagerTest {

    @Test
    void 요청의_첫_줄_정보로_controller를_찾아서_실행시킨다() {
        // given
        DynamicControllerManager dynamicControllerManager = new DynamicControllerManager();
        dynamicControllerManager.add("/login", new LoginController());
        Request request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        Session session = new Session("id");
        when(request.getPath()).thenReturn("/login");
        when(request.getSession(true)).thenReturn(session);
        when(request.getSession()).thenReturn(session);
        when(request.getHttpMethod()).thenReturn(HttpMethod.GET);

        // when
        dynamicControllerManager.service(request, response);

        // then
        assertThat(response.getStaticResourcePath()).isEqualTo("/login.html");
    }
}
