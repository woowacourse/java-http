package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class RequestTest {

    @Test
    void from() {
        Request request = Request.from("GET /index.html HTTP/1.1");
        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo("GET"),
                () -> assertThat(request.getRequestUrl()).isEqualTo("/index.html"),
                () -> assertThat(request.getVersion()).isEqualTo("HTTP/1.1")
        );
    }

    @Test
    void getRequestExtension() {
        Request request = Request.from("GET /index.html HTTP/1.1");
        assertThat(request.getRequestExtension().get()).isEqualTo("html");
    }

    @Test
    void getEmptyRequestExtension() {
        Request request = Request.from("GET /api/login HTTP/1.1");
        assertThat(request.getRequestExtension().isEmpty()).isTrue();
    }
}
