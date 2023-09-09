package org.apache.coyote.http11.common.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.common.HttpHeaderName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestHeadersTest {

    @Test
    void 문자열_리스트를_받아_HttpHeaders를_만든다() {
        // given
        List<String> strings = List.of(
                "Content-Length: 12",
                "Content-Type: text/css");

        // when
        RequestHeaders headers = RequestHeaders.create(strings);

        // then
        assertThat(headers.getHeader("Content-Length")).isEqualTo("12");
        assertThat(headers.getHeader("Content-Type")).isEqualTo("text/css");
    }
}
