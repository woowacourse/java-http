package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class Http11RequestHeadersTest {

    @Test
    @DisplayName("HttpRequest 헤더를 파싱해 생성할 수 있다.")
    void from() {
        String requestHeaders = """
                cache-control: no-cache, private
                connection: keep-alive
                content-type: text/html; charset=UTF-8
                date: Thu, 12 Sep 2024 07:59:42 GMT
                server: nginx
                """;

        Http11RequestHeaders headers = Http11RequestHeaders.from(requestHeaders);
        assertAll(() -> {
            assertThat(headers.get("cache-control")).isEqualTo("no-cache, private");
            assertThat(headers.get("connection")).isEqualTo("keep-alive");
            assertThat(headers.get("content-type")).isEqualTo("text/html; charset=UTF-8");
            assertThat(headers.get("date")).isEqualTo("Thu, 12 Sep 2024 07:59:42 GMT");
            assertThat(headers.get("server")).isEqualTo("nginx");
        });
    }

    @Test
    @DisplayName("Content-Length 값을 가져올 수 있다.")
    void getContentLength() {
        Http11RequestHeaders headers = new Http11RequestHeaders(Map.of("Content-Length", "333"));

        assertThat(headers.getContentLength()).isEqualTo(333);
    }

    @Test
    @DisplayName("Content-Length가 없을 경우 0을 반환한다.")
    void getContentLength_notExist() {
        Http11RequestHeaders headers = new Http11RequestHeaders(new HashMap<>());

        assertThat(headers.getContentLength()).isEqualTo(0);
    }
}
