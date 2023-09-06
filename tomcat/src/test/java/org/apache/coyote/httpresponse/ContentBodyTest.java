package org.apache.coyote.httpresponse;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class ContentBodyTest {

    @Test
    void 빈_내용을_요청하면_빈_내용을_생성한다() {
        // given, when
        final ContentBody contentBody = ContentBody.noContent();

        // then
        assertThat(contentBody.getValue()).isBlank();
    }
}
