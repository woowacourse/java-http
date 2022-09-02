package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void request() {
        HttpRequest httpRequest = HttpRequest.of("GET /index.html HTTP/1.1");

        assertAll(
                () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getUri()).isEqualTo("/index.html"),
                () -> assertThat(httpRequest.getVersion()).isEqualTo(HttpVersion.HTTP_1_1)
        );
    }
}