package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HeadersTest {

    @Test
    void from() {
        String headers = String.join("\r\n",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 100 ");
        InputStream inputStream = new ByteArrayInputStream(headers.getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Headers actual = Headers.from(bufferedReader);

        assertThat(actual.getValue()).containsAllEntriesOf(Map.of(
                "Content-Type", "text/html;charset=utf-8",
                "Content-Length", "100"));
    }

    @Test
    void getHeaders() {
        Headers headers = Headers.builder()
                .contentType(ContentType.TEXT_HTML)
                .contentLength(100);

        assertThat(headers.getHeaders()).isEqualTo(String.join("\r\n",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 100 "));
    }
}
