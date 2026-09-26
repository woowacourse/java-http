package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class StaticResourceControllerTest {

    @Test
    void 루트_경로의_응답을_작성한다() throws Exception {
        Controller controller = new StaticResourceController();
        HttpRequest request = request("GET / HTTP/1.1");
        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        String actual = new String(
                response.toByteArray(),
                StandardCharsets.UTF_8
        );

        assertThat(actual).startsWith("HTTP/1.1 200 OK");
        assertThat(actual).contains("Content-Type: text/html");
        assertThat(actual).endsWith("Hello world!");
    }

    @Test
    void 요청_경로에_대응하는_정적_리소스를_응답한다() throws Exception {
        Controller controller = new StaticResourceController();
        HttpRequest request = request("GET /index.html HTTP/1.1");
        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        String actual = new String(
                response.toByteArray(),
                StandardCharsets.UTF_8
        );

        assertThat(actual).contains("Content-Type: text/html");
        assertThat(actual).doesNotEndWith("Hello world!");
    }

    @Test
    void CSS_리소스에는_CSS_Content_Type을_포함한다() throws Exception {
        Controller controller = new StaticResourceController();
        HttpRequest request = request("GET /css/styles.css HTTP/1.1");
        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        String actual = new String(
                response.toByteArray(),
                StandardCharsets.UTF_8
        );

        assertThat(actual)
                .contains("Content-Type: text/css;charset=utf-8");
    }

    private HttpRequest request(final String requestLine)
            throws Exception {
        String rawRequest = String.join("\r\n",
                requestLine,
                "",
                ""
        );

        return HttpRequest.readFrom(
                new ByteArrayInputStream(
                        rawRequest.getBytes(StandardCharsets.UTF_8)
                )
        );
    }
}
