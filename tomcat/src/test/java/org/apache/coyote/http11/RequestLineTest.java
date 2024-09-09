package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineTest {

    @Test
    @DisplayName("요청된 requestLine을 통해 경로를 알수 있다.")
    void getPath() {
        RequestLine requestLine = new RequestLine("GET /login?account=gugu&password=password HTTP/1.1");

        String path = requestLine.getPath();

        assertThat(path).isEqualTo("/login");
    }
}
