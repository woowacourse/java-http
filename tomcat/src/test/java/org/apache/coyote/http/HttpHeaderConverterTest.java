package org.apache.coyote.http;

import static org.apache.coyote.http.MediaType.TEXT_HTML;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpHeaderConverterTest {

    @Test
    void decode_스트링을_http_header로_변환한다() {
        String string = "Content-Length: 15  \r\nContent-Type: text/html;charset=utf-8";

        HttpHeader httpHeader = HttpHeaderConverter.decode(string);

        assertAll(
            () -> assertThat(httpHeader.getCharset()).isSameAs(StandardCharsets.UTF_8),
            () -> assertThat(httpHeader.getMediaType()).isSameAs(TEXT_HTML),
            () -> assertThat(httpHeader.getContentLength()).isSameAs(15)
        );
    }

    @Test
    void encode_헤더를_문자열로_변환한다() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("Content-Length", List.of("15"));
        map.put("Content-Type", List.of("text/html;charset=utf-8"));
        HttpHeader header = HttpHeader.from(map);

        String encoded = HttpHeaderConverter.encode(header);

        assertAll(
            () -> assertThat(encoded).containsIgnoringCase("Content-Length: 15"),
            () -> assertThat(encoded).containsIgnoringCase("Content-Type: text/html;charset=utf-8")
        );
    }
}