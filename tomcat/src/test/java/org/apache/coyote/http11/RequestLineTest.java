package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RequestLineTest {

    @Test
    void parsesRequestLine() {
        RequestLine requestLine =
                new RequestLine("GET /login?next=home HTTP/1.1");

        assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getRequestUri().getPath()).isEqualTo("/login");
        assertThat(requestLine.getRequestUri().getQueryParameter("next"))
                .isEqualTo("home");
        assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1");
    }
}
