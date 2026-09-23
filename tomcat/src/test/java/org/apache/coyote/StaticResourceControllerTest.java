package org.apache.coyote;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class StaticResourceControllerTest {

    private final StaticResourceController controller =
            new StaticResourceController();

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
    void 로그인_페이지를_반환한다() throws Exception {

        // given
        final HttpRequest request = createRequest("GET /login HTTP/1.1");

        final HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        final String result = writeResponse(response);

        assertThat(result)
                .contains("HTTP/1.1 200 OK")
                .contains("<title>로그인</title>");
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