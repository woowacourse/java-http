package org.apache.coyote.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class RequestStartLineTest {

    @Test
    void 리퀘스트_첫째줄이_정확하게_파싱되는지_확인한다() {
        final String starLine = "GET / HTTP/1.1 ";
        final RequestStartLine requestStartLine = RequestStartLine.from(starLine);

        assertThat(requestStartLine).hasToString(starLine);
    }

    @Test
    void 쿼리가_있는_요청인_경우_파싱_확인() {
        final String starLine = "GET /login?name=kero&password=keroro HTTP/1.1 ";
        final RequestStartLine requestStartLine = RequestStartLine.from(starLine);

        assertThat(requestStartLine).hasToString(starLine);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/index.html", "/index.css"})
    void 정적_파일_요청인_경우_파싱을_확인한다(String url) {
        final String starLine = String.format( "GET %s HTTP/1.1 ", url);
        final RequestStartLine requestStartLine = RequestStartLine.from(starLine);

        assertThat(requestStartLine).hasToString(starLine);
    }
}
