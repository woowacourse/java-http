package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http.HttpHeader;
import org.junit.jupiter.api.Test;
import support.RequestFixture;

class HttpRequestDecoderTest {

    @Test
    void decode_HttpRequest를_만든다() {
        HttpRequest expected = new HttpRequest(HttpRequestLine.decode(
            HttpMethod.GET,
            "/index.html",
            Collections.emptyMap(),
            "HTTP/1.1"
        ), HttpHeader.from(Collections.emptyMap()), Collections.emptyMap());
        HttpRequestDecoder httpRequestDecoder = new HttpRequestDecoder();

        HttpRequest actual = httpRequestDecoder.decode(
            new ByteArrayInputStream("GET /index.html HTTP/1.1 ".getBytes()));

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void decode_HttpRequest를_만든다_파라미터가_있는_경우() {
        Map<String, List<String>> header = new HashMap<>();
        header.put("Content-Length", List.of("12"));
        header.put("Content-Type", List.of("application/x-www-form-urlencoded;charset=utf-8"));

        HttpRequest expected = new HttpRequest(HttpRequestLine.decode(
            HttpMethod.GET,
            "/index.html",
            Map.of("hello", "world"),
            "HTTP/1.1"
        ), HttpHeader.from(header), Map.of("second", "world"));
        HttpRequestDecoder httpRequestDecoder = new HttpRequestDecoder();

        HttpRequest actual = httpRequestDecoder.decode(
            new ByteArrayInputStream(("GET /index.html?hello=world HTTP/1.1  \r\n"
                + "Content-Length: 12 \r\n"
                + "Content-Type: application/x-www-form-urlencoded;charset=utf-8\r\n\r\n"
                + "second=world").getBytes()));

        assertThat(actual).isEqualTo(expected);
    }
}