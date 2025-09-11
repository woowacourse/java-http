package com.techcourse.http.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.NotFoundException;
import org.apache.coyote.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentTypeTest {

    @DisplayName("확장자로 content-type 찾기")
    @Test
    void fromTest1() {
        // given
        String extension = "html";

        // when
        ContentType contentType = ContentType.from(extension);

        // then
        assertThat(contentType).isEqualTo(ContentType.TEXT_HTML);
    }

    @DisplayName("점이 포함된 확장자로 content-type 찾기")
    @Test
    void fromTest2() {
        // given
        String extension = ".css";

        // when
        ContentType contentType = ContentType.from(extension);

        // then
        assertThat(contentType).isEqualTo(ContentType.TEXT_CSS);
    }

    @DisplayName("대문자로 작성된 확장자로 content-type 찾기")
    @Test
    void fromTest3() {
        // given
        String extension = "JS";

        // when
        ContentType contentType = ContentType.from(extension);

        // then
        assertThat(contentType).isEqualTo(ContentType.APPLICATION_JAVASCRIPT);
    }

    @DisplayName("공백이 포함된 확장자로 content-type 찾기")
    @Test
    void fromTest4() {
        // given
        String extension = "  json  ";

        // when
        ContentType contentType = ContentType.from(extension);

        // then
        assertThat(contentType).isEqualTo(ContentType.APPLICATION_JSON);
    }

    @DisplayName("존재하지 않는 확장자인 경우")
    @Test
    void fromTest5() {
        // given
        String extension = "unknown";

        // when & then
        assertThatThrownBy(() -> ContentType.from(extension))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 Content type 확장자입니다: unknown");
    }
}
