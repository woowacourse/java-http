package org.apache.catalina.resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.data.ContentType;
import org.apache.coyote.http11.data.HttpMethod;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.HttpStatusCode;
import org.apache.coyote.HttpVersion;
import org.apache.coyote.http11.data.MediaType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceHandlerTest {
    private final ResourceHandler resourceHandler = ResourceHandler.getInstance();

    @Test
    @DisplayName("html 파일을 조회한다.")
    void getHtml() throws IOException {
        // given
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/index.html", HttpVersion.HTTP_1_1, null,
                new ContentType(MediaType.HTML, "charset=utf-8"), 0, null, List.of());
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        resourceHandler.service(httpRequest, httpResponse);

        // then
        assertAll(() -> {
            assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.OK);
            assertThat(httpResponse.getContentType().getMediaType()).isEqualTo(MediaType.HTML);
            assertThat(httpResponse.getResponseBody()).isNotEmpty();
        });
    }

    @Test
    @DisplayName("css 파일을 조회한다.")
    void getCss() throws IOException {
        // given
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/css/styles.css", HttpVersion.HTTP_1_1, null,
                new ContentType(MediaType.CSS, null), 0, null, List.of());
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        resourceHandler.service(httpRequest, httpResponse);

        // then
        assertAll(() -> {
            assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.OK);
            assertThat(httpResponse.getContentType().getMediaType()).isEqualTo(MediaType.CSS);
            assertThat(httpResponse.getResponseBody()).isNotEmpty();
        });
    }

    @Test
    @DisplayName("js 파일을 조회한다.")
    void getJs() throws IOException {
        // given
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/assets/chart-bar.js", HttpVersion.HTTP_1_1, null,
                new ContentType(MediaType.JAVASCRIPT, null), 0, null, List.of());
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        resourceHandler.service(httpRequest, httpResponse);

        // then
        assertAll(() -> {
            assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.OK);
            assertThat(httpResponse.getContentType().getMediaType()).isEqualTo(MediaType.JAVASCRIPT);
            assertThat(httpResponse.getResponseBody()).isNotEmpty();
        });
    }

    @Test
    @DisplayName("루트 경로를 조회한다.")
    void getRoot() throws IOException {
        // given
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/", HttpVersion.HTTP_1_1, null,
                new ContentType(MediaType.HTML, "charset=utf-8"), 0, null, List.of());
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        resourceHandler.service(httpRequest, httpResponse);

        // then
        assertAll(() -> {
            assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.OK);
            assertThat(httpResponse.getContentType().getMediaType()).isEqualTo(MediaType.HTML);
            assertThat(httpResponse.getResponseBody()).isEqualTo("Hello world!");
        });
    }
}
