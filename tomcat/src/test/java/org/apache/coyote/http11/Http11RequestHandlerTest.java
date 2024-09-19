package org.apache.coyote.http11;

import static org.apache.coyote.http11.Http11HeaderName.CONTENT_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11RequestHandlerTest {

    @DisplayName("request, response를 받아서 처리한다.")
    @Test
    void handle() throws IOException {
        String requestHeaderString = "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 9\n" +
                "Accept: text/html\n" +
                "\n";

        Http11Request request = Http11Request.from(
                new ByteArrayInputStream(requestHeaderString.getBytes(StandardCharsets.UTF_8)));

        Http11Response response = Http11Response.of(request);

        Http11RequestHandler requestHandler = Http11RequestHandler.from();

        requestHandler.handle(request, response);

        assertThat(response.getStatusLine().getStatusLine()).isEqualTo("HTTP/1.1 200 OK ");
        assertThat(response.getResponseHeader().getHeader(CONTENT_TYPE.getName())).isEqualTo("text/html;charset=utf-8");
    }

    @DisplayName("정적 페이지를 요청하면 404 notFound를 응답한다.")
    @Test
    void handle2() throws IOException {
        String requestHeaderString = "GET /401.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Accept: text/html\n" +
                "\n";

        Http11Request request = Http11Request.from(
                new ByteArrayInputStream(requestHeaderString.getBytes(StandardCharsets.UTF_8)));

        Http11Response response = Http11Response.of(request);

        Http11RequestHandler requestHandler = Http11RequestHandler.from();

        requestHandler.handle(request, response);

        assertThat(response.getStatusLine().getStatusLine()).isEqualTo("HTTP/1.1 404 NOT_FOUND ");
        assertThat(response.getResponseHeader().getHeader(CONTENT_TYPE.getName())).isEqualTo("text/html;charset=utf-8");
    }
}
