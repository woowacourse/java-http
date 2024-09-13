package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHeaderTest {

    @Test
    @DisplayName("requestHeader를 생성한다")
    void createRequestHeader() {
        var result = new HttpRequestHeader(List.of("Host: localhost:8080", "Connection: keep-alive", "Content-Length: 80", "Content-Type: application/x-www-form-urlencoded", "Accept: */*"));

        assertThat(result.getHeaders()).hasSize(5);
        assertThat(result.getHeaders().get("Host")).isEqualTo("localhost:8080");
        assertThat(result.getHeaders().get("Content-Type")).isEqualTo("application/x-www-form-urlencoded");
    }
}
