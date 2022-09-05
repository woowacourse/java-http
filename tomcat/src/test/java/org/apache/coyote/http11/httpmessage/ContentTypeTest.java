package org.apache.coyote.http11.httpmessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @Test
    void 일치하는_이름의_type이_존재하면_해당하는_ContrentType을_반환한다() {
        // given
        String type = "html";

        // when
        ContentType contentType = ContentType.of(type);

        // then
        assertThat(contentType).isEqualTo(ContentType.HTML);
    }

    @Test
    void 일치하는_value의_type이_존재하면_해당하는_ContrentType을_반환한다() {
        // given
        String value = "text/html";

        // when
        ContentType contentType = ContentType.of(value);

        // then
        assertThat(contentType).isEqualTo(ContentType.HTML);
    }

    @Test
    void 일치하는_이름의_type이_존재하지_않으면_예외를_던진다() {
        // given
        String invalidType = "invalidType";

        // when & then
        assertThatThrownBy(() -> ContentType.of(invalidType))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
