package org.apache.coyote.http11.message.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void 확장자에_따라_알맞는_ContentType을_반환한다() {
        // given
        String path = "index.html";

        // when
        ContentType actual = ContentType.fromPath(path);

        // then
        assertThat(actual).isEqualTo(ContentType.HTML);
    }

    @Test
    void 등록되지_않은_확장자면_기본_MIME_타입을_반환한다() {
        // given
        String path = "index.test";

        // when
        ContentType actual = ContentType.fromPath(path);

        // then
        assertThat(actual).isEqualTo(ContentType.DEFAULT);
    }

    @Test
    void Mime_타입에_따라_알맞는_ContentType을_반환한다() {
        // given
        String mimeType = "application/x-www-form-urlencoded";

        // when
        ContentType actual = ContentType.fromMimeType(mimeType);

        // then
        assertThat(actual).isEqualTo(ContentType.FORM_URLENCODED);
    }

    @Test
    void 등록되지_않은_Mime_타입이면_기본_MIME_타입을_반환한다() {
        // given
        String mimeType = "non-exists";

        // when
        ContentType actual = ContentType.fromMimeType(mimeType);

        // then
        assertThat(actual).isEqualTo(ContentType.DEFAULT);
    }
}
