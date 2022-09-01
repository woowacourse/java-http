package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    @DisplayName("헤더 라인 리스트로 헤더 클래스를 만든다.")
    void from() {
        final List<String> rawHeaders = List.of(
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Type: application/json"
        );
        final HttpHeaders headers = HttpHeaders.from(rawHeaders);

        assertAll(
                () -> assertThat(headers.getValue("Host").get()).isEqualTo("localhost:8080"),
                () -> assertThat(headers.getValue("Connection").get()).isEqualTo("keep-alive"),
                () -> assertThat(headers.getValue("Content-Type").get()).isEqualTo("application/json")
        );
    }
}