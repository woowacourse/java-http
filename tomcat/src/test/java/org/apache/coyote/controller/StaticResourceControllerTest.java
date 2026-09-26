package org.apache.coyote.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.resource.ResourceReader;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StaticResourceControllerTest {

    private final ResourceReader resourceReader = new ResourceReader();

    private final StaticResourceController controller = new StaticResourceController(resourceReader);

    @Test
    void 루트_요청에_Hello_world를_응답한다() throws Exception {

        // given
        final HttpRequest request = createRequest("GET / HTTP/1.1");

        final HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        final String result = writeResponse(response);

        assertThat(result)
                .contains("HTTP/1.1 200 OK")
                .contains("Hello world!");
    }

    @Test
    void forward_경로가_있으면_해당_리소스를_응답한다() throws Exception {

        // given
        final HttpRequest request = createRequest("GET /login HTTP/1.1");

        final HttpResponse response = new HttpResponse();
        response.forward("/login.html");

        // when
        controller.service(request, response);

        // then
        final String result = writeResponse(response);

        assertThat(result)
                .contains("HTTP/1.1 200 OK")
                .contains("<title>로그인</title>");
    }

    @Test
    void 애플리케이션_경로는_알지_못해_404를_응답한다() throws Exception {

        // given
        final HttpRequest request = createRequest("GET /login HTTP/1.1");

        final HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(writeResponse(response)).contains("HTTP/1.1 404 Not Found");
    }

    @Test
    void 존재하지_않는_리소스는_404를_응답한다() throws Exception {

        // given
        final HttpRequest request =
                createRequest("GET /not-found.html HTTP/1.1");

        final HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        final String result = writeResponse(response);

        assertThat(result)
                .contains("HTTP/1.1 404 Not Found")
                .contains("Not Found");
    }

    @Test
    void CSS_리소스의_Content_Type을_응답한다() throws Exception {

        // given
        final HttpRequest request = createRequest("GET /css/styles.css HTTP/1.1");

        final HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        final String result = writeResponse(response);

        assertThat(result)
                .contains("HTTP/1.1 200 OK").contains("Content-Type: text/css");
    }

    private HttpRequest createRequest(final String requestLine) throws Exception {

        final String rawRequest = String.join(
                "\r\n",
                requestLine,
                "Host: localhost:8080",
                "",
                ""
        );

        final ByteArrayInputStream inputStream =
                new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8));

        return HttpRequest.from(inputStream).orElseThrow();
    }

    private String writeResponse(final HttpResponse response) throws Exception {

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        response.writeTo(outputStream);

        return outputStream.toString(StandardCharsets.UTF_8);
    }
}