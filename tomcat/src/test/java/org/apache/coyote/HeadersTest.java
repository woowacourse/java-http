package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import org.junit.jupiter.api.Test;

class HeadersTest {

    @Test
    void 요청_헤더를_저장한다() {
        List<String> rowHeaders = List.of("Host: localhost:8080", "Connection: keep-alive");
        Headers headers = Headers.from(rowHeaders);

        assertAll(
                () -> assertThat(headers.getValues().get("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(headers.getValues().get("Connection")).isEqualTo("keep-alive")
        );
    }
}
