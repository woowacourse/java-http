package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.util.HttpHeaderParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    @DisplayName("응답 헤더 문자열로 반환한다.")
    void invalidHeader() {
        HttpHeaders headers = getHeaders();
        String result = "Connection: keep-alive \r\n"
                + "Host: localhost:8080 \r\n";

        assertThat(headers.getResponse()).isEqualTo(result);
    }

    private HttpHeaders getHeaders() {
        return HttpHeaderParser.parse(List.of(
                new String[]{"Host:", "localhost:8080"},
                new String[]{"Connection:", "keep-alive"}));
    }
}
