package org.apache.coyote.http.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.MediaType;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.Test;
import support.RequestFixture;

class ViewRendererTest {

    @Test
    void render_forward한_경우_forward_경로를_찾는다() {
        HttpRequest request = RequestFixture.GET.buildRequestToResource("/assets/chart-area.js");
        HttpResponse response = new HttpResponse();
        ViewRenderer viewRenderer = new ViewRenderer();

        response.forward("/index.html");
        viewRenderer.render(request, response);

        assertThat(response.getContentType().get().getMediaType()).isEqualTo(MediaType.TEXT_HTML);
    }

    @Test
    void render_각_확장자에_맞게_response_구성한다1() {
        HttpRequest request = RequestFixture.GET.buildRequestToResource("/assets/chart-area.js");
        HttpResponse response = new HttpResponse();
        ViewRenderer viewRenderer = new ViewRenderer();

        viewRenderer.render(request, response);

        assertThat(response.getContentType().get().getMediaType()).isEqualTo(MediaType.TEXT_JAVASCRIPT);
    }

    @Test
    void render_각_확장자에_맞게_response_구성한다2() {
        HttpRequest request = RequestFixture.GET.buildRequestToResource("/index.html");
        HttpResponse response = new HttpResponse();
        ViewRenderer viewRenderer = new ViewRenderer();

        viewRenderer.render(request, response);

        assertThat(response.getContentType().get().getMediaType()).isEqualTo(MediaType.TEXT_HTML);
    }
}