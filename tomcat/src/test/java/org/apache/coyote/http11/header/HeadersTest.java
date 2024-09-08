package org.apache.coyote.http11.header;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HeadersTest {

    @Test
    @DisplayName("`: `을 기반으로 구분한다.")
    void parse_with_colon_and_space() {
        final String line = "Host: localhost:8080 ";
        final Headers headers = new Headers();
        headers.put(line);

        assertThat(headers.get("Host")).isEqualTo("localhost:8080 ");

    }
}
