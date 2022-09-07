package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    void 헤더_파싱_테스트() {
        // given
        List<String> headerLines = List.of("Name: value");

        // when
        HttpHeaders httpHeaders = HttpHeaders.parse(headerLines);

        // then
        assertThat(httpHeaders.getAll()).hasSize(1)
                .containsOnly(HttpHeader.parse("Name: value"));
    }
}
