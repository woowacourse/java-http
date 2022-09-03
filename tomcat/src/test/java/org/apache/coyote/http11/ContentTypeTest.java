package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.apache.coyote.http11.exception.ContentNotFoundException;

class ContentTypeTest {

    @DisplayName("html에 맞는 content-type을 찾는다.")
    @Test
    void findHtmlType() {
        final ContentType type = ContentType.matchMIMEType("html");

        assertThat(type).isEqualTo(ContentType.HTML);
    }

    @DisplayName("css에 맞는 content-type을 찾는다.")
    @Test
    void findCssType() {
        final ContentType type = ContentType.matchMIMEType("css");

        assertThat(type).isEqualTo(ContentType.CSS);
    }

    @DisplayName("매칭이 되는 content-type이 없는 경우 예외가 발생한다.")
    @Test
    void notFoundContentType() {
        assertThatThrownBy(() -> ContentType.matchMIMEType(""))
                .hasMessageContaining("지원하지 않는 Content-Type 입니다.")
                .isInstanceOf(ContentNotFoundException.class);
    }
}
