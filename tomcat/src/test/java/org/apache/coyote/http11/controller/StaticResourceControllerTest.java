package org.apache.coyote.http11.controller;

import common.http.HttpMethod;
import common.http.HttpStatus;
import common.http.Request;
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
class StaticResourceControllerTest {

    @Test
    void 요청에_정적리소스_경로가_있을_경우_정적리소스를_제공한다() {
        // given
        Request request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getVersionOfTheProtocol()).thenReturn("HTTP/1.1");
        when(request.getPath()).thenReturn("/login.html");
        when(request.getHttpMethod()).thenReturn(HttpMethod.GET);

        StaticResourceController staticResourceController = new StaticResourceController();

        // when
        staticResourceController.service(request, response);

        // then
        assertThat(response.getMessage()).contains("HTTP/1.1 200 OK");
    }

    @Test
    void 응답에_정적리소스_경로가_있을_경우_정적리소스를_제공한다() {
        // given
        Request request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        response.addVersionOfTheProtocol("HTTP/1.1");
        response.addStaticResourcePath("/login.html");
        response.addHttpStatus(HttpStatus.OK);

        StaticResourceController staticResourceController = new StaticResourceController();

        // when
        staticResourceController.service(request, response);

        // then
        assertThat(response.getMessage()).contains("HTTP/1.1 200 OK");
    }
}
