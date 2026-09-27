package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;

class StaticResourceControllerTest {

    @Test
    void servesIndexHtml() throws Exception {
        //given
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        HttpRequest request = request("/index.html");

        //when
        new StaticResourceController().service(request, new HttpResponse(output));

        //then
        String response = output.toString(StandardCharsets.UTF_8);
        assertThat(response).startsWith("HTTP/1.1 200 OK\r\nContent-Type: text/html;charset=utf-8\r\n");
        assertThat(response).contains("<title>대시보드</title>");
    }

    @Test
    void servesCssWithCssContentType() throws Exception {
        //given
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        //when
        new StaticResourceController().service(request("/css/styles.css"), new HttpResponse(output));

        //then
        String response = output.toString(StandardCharsets.UTF_8);
        assertThat(response).startsWith("HTTP/1.1 200 OK\r\nContent-Type: text/css;charset=utf-8\r\n");
        assertThat(response).contains("body");
    }

    private HttpRequest request(String path) {
        return new HttpRequest(new RequestLine("GET", path, "", "HTTP/1.1"), Map.of(), "");
    }
}
