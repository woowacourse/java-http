package org.apache.coyote.http11.common.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class StartLineTest {

    @Test
    void 문자열을_받아_StartLine을_만들_수_있다() {
        // given
        String input = "GET /index.html HTTP/1.1";

        // when
        StartLine startLine = StartLine.create(input);

        // then
        assertThat(startLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(startLine.getUri().getValue()).isEqualTo("/index.html");
    }
}
