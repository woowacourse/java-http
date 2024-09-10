package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.ContentType.Charset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class ContentTypeTest {

    @Test
    @DisplayName("콘텐츠 타입 응답 스트링 확인")
    void responseText() {
        final ContentType contentType = ContentType.TEXT_HTML;

        assertThat(contentType.getResponseText(Charset.UTF_8)).isEqualTo("Content-Type: text/html; charset=utf-8");
    }

    @ParameterizedTest
    @DisplayName("콘텐츠 타입 변환 확인")
    @EnumSource(ContentType.class)
    void from(final ContentType contentType) {
        //given
        final ContentType find = ContentType.from(contentType.getValue());

        assertThat(find).isEqualTo(contentType);
    }
}
