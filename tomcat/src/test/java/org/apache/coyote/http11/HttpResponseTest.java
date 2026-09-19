package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    void 응답_메시지를_생성한다() {
        final HttpResponse response = new HttpResponse(HttpStatus.OK, "text/html", "Hello world!");

        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(toString(response)).isEqualTo(expected);
    }

    @Test
    void 상태_코드에_맞는_상태_라인을_생성한다() {
        final HttpResponse response = new HttpResponse(HttpStatus.NOT_FOUND, "text/html", "");

        assertThat(toString(response)).startsWith("HTTP/1.1 404 Not Found \r\n");
    }

    @Test
    void Content_Length는_바이트_길이로_계산한다() {
        final HttpResponse response = new HttpResponse(HttpStatus.OK, "text/html", "한글");

        assertThat(toString(response)).contains("Content-Length: 6 \r\n");
    }

    private String toString(HttpResponse response) {
        return new String(response.getBytes(), UTF_8);
    }
}