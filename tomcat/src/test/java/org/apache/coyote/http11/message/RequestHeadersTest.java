package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @Test
    @DisplayName("메세지의 header 줄들로부터 RequestHeaders 를 생성한다.")
    void from() {
        // given
        final List<String> headerLines = List.of("Host: localhost:8080 ", "Connection: keep-alive ");

        // when
        final RequestHeaders headers = RequestHeaders.from(headerLines);

        // then
        assertThat(headers.getHeadersWithValue())
            .containsExactlyInAnyOrderEntriesOf(Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive"
            ));
    }
}
