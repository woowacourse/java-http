package org.apache.coyote.http11.model.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("쿼리스트링이 없는 url에서 올바른 url과 빈 쿼리 파라미터를 추출한다")
    @Test
    void extractUrl() {
        final var rawRequestLine = "GET /login HTTP/1.1 ";

        final var requestLine = new RequestLine(rawRequestLine);

        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo("GET"),
                () -> assertThat(requestLine.getUrl()).isEqualTo("/login"),
                () -> assertThat(requestLine.getQueryParams()).isEmpty()
        );
    }

    @DisplayName("쿼리스트링이 있는 url에서 올바른 url과 쿼리 파라미터를 추출한다")
    @Test
    void extractUrlAndQueryParams() {
        final var rawRequestLine = "GET /login?account=gugu&password=password HTTP/1.1 ";

        final var requestLine = new RequestLine(rawRequestLine);

        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo("GET"),
                () -> assertThat(requestLine.getUrl()).isEqualTo("/login"),
                () -> assertThat(requestLine.getQueryParams()).isEqualTo(
                        Map.of("account", "gugu", "password", "password"))
        );
    }
}
