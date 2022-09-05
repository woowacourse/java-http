package org.apache.http;

import nextstep.jwp.http.Headers;
import nextstep.jwp.support.View;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HeadersTest {

    @Test
    void 기본_헤더가_포함되어_있다() {
        // given
        final String expected = "Content-Type: */* \r\n" +
                "Content-Length: 0 \r\n";

        // when
        final String actual = new Headers().parse();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 헤더를_추가하면_해당_내용까지_포함하여_파싱한다() {
        // given
        final String expected = "Content-Type: text/html \r\n" +
                "Content-Length: 1234 \r\n" +
                "Location: /index.html \r\n";

        // when
        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue());
        headers.put(HttpHeader.CONTENT_LENGTH, "1234");
        headers.put(HttpHeader.LOCATION, View.INDEX.getValue());
        final String actual = headers.parse();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
