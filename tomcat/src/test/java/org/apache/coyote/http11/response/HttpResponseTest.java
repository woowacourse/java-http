package org.apache.coyote.http11.response;

import org.apache.coyote.http11.StaticResource;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {
    @Test
    void toBytes() {
        // given
        final HttpResponse response = HttpResponse.of(HttpStatus.OK, "text/html", "Hello world!");

        // when
        final String actual = new String(response.toBytes(), StandardCharsets.UTF_8);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void contentLengthIsByteLength() {
        // given
        final HttpResponse response = HttpResponse.of(HttpStatus.BAD_REQUEST, "text/plain", "잘못된 요청");

        // when
        final String actual = new String(response.toBytes(), StandardCharsets.UTF_8);

        // then
        assertThat(actual)
                .startsWith("HTTP/1.1 400 Bad Request ")
                .contains("Content-Length: 16 ");
    }

    @Test
    void fromStaticResource() {
        // given
        final StaticResource staticResource = new StaticResource("body { }", "text/css");

        // when
        final HttpResponse response = HttpResponse.of(HttpStatus.OK, staticResource);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: 8 ",
                "",
                "body { }");
        assertThat(new String(response.toBytes(), StandardCharsets.UTF_8)).isEqualTo(expected);
    }
}
