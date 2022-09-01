package org.apache.coyote.http11.model.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("문자열에서 올바른 값이 추출된다")
    @Test
    void extractValues() {
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
