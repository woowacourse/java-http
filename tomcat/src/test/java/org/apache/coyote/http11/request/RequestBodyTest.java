package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    void createRequestBody() {
        // given
        List<String> lines = List.of("hello", "world");
        // when
        RequestBody body = RequestBody.parse(lines);
        // then
        assertThat(body.getBody()).isEqualTo("hello\nworld");
    }
}
