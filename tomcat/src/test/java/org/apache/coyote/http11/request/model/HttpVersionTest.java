package org.apache.coyote.http11.request.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpVersionTest {

    @Test
    @DisplayName("http 버전을 조회한다.")
    void ofHTTP1_1() {
        HttpVersion version = HttpVersion.of("HTTP/1.1");

        assertThat(version).isEqualTo(HttpVersion.HTTP_1_1);
    }
}
