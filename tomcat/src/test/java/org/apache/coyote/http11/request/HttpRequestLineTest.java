package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.request.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestLineTest {

    @Test
    @DisplayName("requestLine을 생성한다.")
    void createRequestLine() {
        var result = new HttpRequestLine("POST /register HTTP/1.1");

        assertThat(result.getMethod()).isEqualTo(POST);
        assertThat(result.getPath()).isEqualTo("/register");
        assertThat(result.getVersion()).isEqualTo("HTTP/1.1");
    }

}
