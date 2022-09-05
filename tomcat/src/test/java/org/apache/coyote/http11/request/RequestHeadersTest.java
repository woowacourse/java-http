package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @Test
    void parseRequestHeaders() {
        // given
        List<String> lines = List.of(
                "Content-Type: application/json",
                "Referrer Policy: strict-origin-when-cross-origin"
        );

        // when
        RequestHeaders headers = RequestHeaders.parse(lines);

        // then
        Assertions.assertAll(
                () -> assertThat(headers.getPairByField("Content-Type")).isEqualTo("Content-Type: application/json"),
                () -> assertThat(headers.getPairByField("Referrer Policy")).isEqualTo(
                        "Referrer Policy: strict-origin-when-cross-origin")
        );
    }

}
