package org.apache.coyote.httpresponse.header;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class ContentTypeHeaderTest {

    @Test
    void 파일_경로를_통해_컨텐츠_헤더의_타입을_추론한다() {
        // given
        final String path = "static/style.css";

        // when
        final ContentTypeHeader actual = ContentTypeHeader.from(path);
        final ContentTypeHeader expected = ContentTypeHeader.TEXT_CSS;

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 지원하지_않는_타입은_text_html_타입으로_반환한다() {
        // given
        final String path = "main.java";

        // when
        final ContentTypeHeader actual = ContentTypeHeader.from(path);
        final ContentTypeHeader expected = ContentTypeHeader.TEXT_HTML;

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 올바른_형식의_헤더로_응답한다() {
        // given
        final ContentTypeHeader contentTypeHeader = ContentTypeHeader.from("index.html");

        // when
        final String actual = contentTypeHeader.getKeyAndValue(ResponseHeaderType.CONTENT_TYPE);
        final String expected = "Content-Type: text/html;charset=utf-8";

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
