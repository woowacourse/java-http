package org.apache.coyote.http11;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpHeadersTest {

    @Test
    @DisplayName("헤더 이름의 대소문자를 구분하지 않고 값을 조회한다")
    void getsHeaderRegardlessOfNameCase() throws IOException {
        final HttpHeaders headers = new HttpHeaders(List.of(
                "Host: localhost:8080",
                "Content-Type: text/plain"));

        assertThat(headers.getHeader("host")).isEqualTo("localhost:8080");
        assertThat(headers.getHeader("CONTENT-TYPE")).isEqualTo("text/plain");
    }

    @Test
    @DisplayName("콜론으로 구분되지 않은 헤더를 거부한다")
    void rejectsHeaderWithoutColon() {
        assertThatThrownBy(() -> new HttpHeaders(List.of("Host localhost")))
                .isInstanceOf(IOException.class);
    }
}
