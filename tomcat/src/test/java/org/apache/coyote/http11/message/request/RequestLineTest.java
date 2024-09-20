package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("쿼리스트링을 제외한 경로를 반환한다.")
    @Test
    void getPath() {
        RequestLine requestLine = new RequestLine(
                HttpMethod.GET,
                "/login?account=gugu&password=password",
                "HTTP/1.1"
        );

        String path = requestLine.getPath();

        assertThat(path).isEqualTo("/login");
    }
}
