package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.util.Map;
import org.apache.coyote.http11.http.domain.ContentType;
import org.apache.coyote.http11.http.domain.Headers;
import org.junit.jupiter.api.Test;
import support.BufferedReaderFactory;

class HeadersTest {

    @Test
    void from() {
        String headers = String.join("\r\n",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 100 ");
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader(headers);
        Headers actual = Headers.from(bufferedReader);

        assertThat(actual.getValue()).containsAllEntriesOf(Map.of(
                "Content-Type", "text/html;charset=utf-8 ",
                "Content-Length", "100 "));
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
