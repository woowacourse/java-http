package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("request line을 파싱해 path를 반환한다.")
    @Test
    void getPath() {
        RequestLine requestLine = new RequestLine("GET /login HTTP/1.1");

        assertThat(requestLine.getPath()).isEqualTo("/login");
    }
}
