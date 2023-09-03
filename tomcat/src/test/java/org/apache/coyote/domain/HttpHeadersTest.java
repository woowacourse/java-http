package org.apache.coyote.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.vo.HttpHeaders;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class HttpHeadersTest {

    @Test
    void 헤더를_실제_response_형태로_변환한다() {
        // given
        HttpHeaders headers = HttpHeaders.getEmptyHeaders();
        headers.putAll(HttpHeader.ACCEPT, List.of("text/html,application/xhtml+xml"));

        // when
        String rawHeaders = headers.getRawHeaders();

        // then
        assertThat(rawHeaders).isEqualTo("Accept: text/html,application/xhtml+xml");
    }
}
