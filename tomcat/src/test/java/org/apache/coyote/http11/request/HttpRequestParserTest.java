package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpRequestParserTest {

    private HttpRequestParser httpRequestParser = HttpRequestParser.getInstance();

    @Test
    void 들어온_요청을_HttpRequest로_변환_본문이_있는_경우() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Accept: text/html",
                "Content-Length: 11",
                "",
                "hello world"
        );

        System.out.println(httpRequest);

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        // when
        HttpRequest actual = httpRequestParser.parse(inputStream);

        // then
        RequestLine requestLine = actual.getRequestLine();
        Map<String, String> header = actual.getHeader();
        RequestBody requestBody = actual.getRequestBody();

        assertAll(
                () -> assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(requestLine.getMethod()).isEqualTo("GET"),
                () -> assertThat(requestLine.getPath()).isEqualTo("/index.html"),
                () -> assertThat(header).size().isEqualTo(2),
                () -> assertThat(header.get("Accept")).isEqualTo("text/html"),
                () -> assertThat(header.get("Content-Length")).isEqualTo("11"),
                () -> assertThat(requestBody.getContent()).isEqualTo("hello world")
        );
    }

    @Test
    void 들어온_요청을_HttpRequest로_변환_본문이_없는_경우() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Accept: text/html",
                "Content-Length: 0"
        );

        System.out.println(httpRequest);

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        // when
        HttpRequest actual = httpRequestParser.parse(inputStream);

        // then
        RequestLine requestLine = actual.getRequestLine();
        Map<String, String> header = actual.getHeader();
        RequestBody requestBody = actual.getRequestBody();

        assertAll(
                () -> assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(requestLine.getMethod()).isEqualTo("GET"),
                () -> assertThat(requestLine.getPath()).isEqualTo("/index.html"),
                () -> assertThat(header).size().isEqualTo(2),
                () -> assertThat(header.get("Accept")).isEqualTo("text/html"),
                () -> assertThat(header.get("Content-Length")).isEqualTo("0"),
                () -> assertThat(requestBody.getContent()).isEmpty()
        );
    }
}
