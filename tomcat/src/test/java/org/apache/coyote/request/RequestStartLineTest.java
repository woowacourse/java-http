package org.apache.coyote.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class RequestStartLineTest {

    @Test
    void 리퀘스트_첫째줄이_정확하게_파싱되는지_확인한다() {
        final String starLine = "GET / HTTP/1.1 ";
        final RequestStartLine requestStartLine = RequestStartLine.from(starLine);

        assertThat(requestStartLine).hasToString(starLine);
    }

}
