package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineTest {

    @Test
    void parsesRequestLine() {
        RequestLine requestLine = RequestLine.parse(
                "GET /login?next=hello+world&email=user%40example.com HTTP/1.1"
        );

        assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getRequestUri().getPath()).isEqualTo("/login");
        assertThat(requestLine.getRequestUri().getQueryParameter("next"))
                .isEqualTo("hello world");
        assertThat(requestLine.getRequestUri().getQueryParameter("email"))
                .isEqualTo("user@example.com");
        assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1");
    }
}
