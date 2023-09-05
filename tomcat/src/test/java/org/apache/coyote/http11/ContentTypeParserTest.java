package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ContentTypeParserTest {

    @Test
    void 확장자에_따른_컨텐츠_타입을_알아낼_수_있다() {
        // given
        String path = "target.html";

        // when
        String contentType = ContentTypeParser.parse(path);

        // then
        assertThat(contentType).isEqualTo("text/html;charset=utf-8");
    }
}
