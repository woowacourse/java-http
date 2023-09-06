package org.apache.coyote.http;

import static org.apache.coyote.http.MediaType.TEXT_HTML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpHeaderConverterTest {

    @Test
    void decode_스트링을_http_header로_변환한다() {
        String string = "Content-Length: 15  \r\nContent-Type: text/html;charset=utf-8";

        HttpHeader httpHeader = HttpHeaderConverter.decode(
            new BufferedReader(new InputStreamReader(new ByteArrayInputStream(string.getBytes(StandardCharsets.UTF_8))))
        );

        assertAll(
            () -> assertThat(httpHeader.getContentType().get().getValue()).isEqualTo("text/html;charset=utf-8")
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