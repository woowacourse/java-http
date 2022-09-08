package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {


    @Test
    void createRequestHeaders() {
        // given
        List<String> lines = List.of("Content-Type: text/html;charset=utf-8", "Content-Length: 12");
        // when
        RequestHeaders headers = RequestHeaders.parse(lines);

        // then
        Assertions.assertAll(
                () -> assertThat(headers.findHeader("Content-Type").getValue()).isEqualTo("text/html;charset=utf-8"),
                () -> assertThat(headers.findHeader("Content-Length").getValue()).isEqualTo("12")
        );
    }

}
