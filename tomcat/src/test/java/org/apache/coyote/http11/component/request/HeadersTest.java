package org.apache.coyote.http11.component.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HeadersTest {

    @Test
    @DisplayName("해더를 파싱한다.")
    void parse_header() {
        // given
        final var plaintext = """
                Host: localhost:8080
                Connection: keep-alive
                Accept: */*
                """;

        // when
        final var header = new Headers(plaintext);

        // then
        assertThat(header.get("Host")).isEqualTo("localhost:8080");
    }

}
