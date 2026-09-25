package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestLineTest {

    @Test
    @DisplayName("메서드, URI, HTTP 버전을 파싱한다")
    void parsesMethodUriAndHttpVersion() {
        final RequestLine requestLine = new RequestLine("POST /login?next=/home HTTP/1.1");

        assertThat(requestLine.getMethod()).isEqualTo("POST");
        assertThat(requestLine.getUri()).isEqualTo("/login?next=/home");
        assertThat(requestLine.getHttpVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    @DisplayName("세 부분으로 구성되지 않은 요청 라인은 거부한다")
    void rejectsRequestLineWithoutThreeParts() {
        assertThatThrownBy(() -> new RequestLine("GET /"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
