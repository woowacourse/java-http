package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestTest {

    @Test
    void HTTP_메시지를_해석한다() {
        String message = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "body");

        HttpRequest httpRequest = HttpRequest.from(toInputStream(message));
        assertThat(httpRequest.getMethod()).isEqualTo("GET");
        assertThat(httpRequest.getPath()).isEqualTo("/index.html");
        assertThat(httpRequest.getVersion()).isEqualTo("HTTP/1.1");
        assertThat(httpRequest.getHeaders())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        new HttpHeader("Host", "localhost:8080"),
                        new HttpHeader("Connection", "keep-alive")
                );
        assertThat(httpRequest.getBody()).isEqualTo("body");
    }

    @Test
    void 바디_안의_CRLF는_바디의_일부로_취급한다() {
        String message = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "line1\r\nline2");

        HttpRequest httpRequest = HttpRequest.from(toInputStream(message));
        assertThat(httpRequest.getBody()).isEqualTo("line1\r\nline2");
    }

    @Test
    void 헤더의_앞_뒤_공백을_무시한다() {
        String message = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest httpRequest = HttpRequest.from(toInputStream(message));

        assertThat(httpRequest.getHeaders()).allSatisfy(
                it -> assertThat(it.getValue()).doesNotEndWith(" ")
        );
    }

    private ByteArrayInputStream toInputStream(String message) {
        return new ByteArrayInputStream(message.getBytes());
    }
}
