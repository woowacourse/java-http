package org.apache.coyote.http11.message.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void 확장자에_따라_알맞는_MIME_타입을_반환한다() {
        // given
        String path = "index.html";

        // when
        String actual = ContentType.getMimeTypeFrom(path);

        // then
        assertThat(actual).isEqualTo(ContentType.HTML.getMimeType());
    }

    @Test
    void 등록되지_않은_확장자면_기본_MIME_타입을_반환한다() {
        // given
        String path = "index.test";

        // when
        String actual = ContentType.getMimeTypeFrom(path);

        // then
        assertThat(actual).isEqualTo(ContentType.DEFAULT.getMimeType());
    }
}
