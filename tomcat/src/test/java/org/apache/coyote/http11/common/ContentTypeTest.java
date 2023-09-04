package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ContentTypeTest {

    @Test
    void 파일명을_입력받아_해당_파일에_해당하는_컨텐츠_타입을_반환한다() {
        // given
        final String fileName = "hello.js";

        // when
        final HttpExtensionType httpExtensionType = HttpExtensionType.from(fileName);

        // then
        assertThat(httpExtensionType).isEqualTo(HttpExtensionType.JS);
    }

    @Test
    void 입력받은_파일에_해당하는_컨텐츠_타입이_없는_경우_HTML타입을_반환한다() {
        // given
        final String fileName = "hello.txt";

        // when
        final HttpExtensionType httpExtensionType = HttpExtensionType.from(fileName);

        // then
        assertThat(httpExtensionType).isEqualTo(HttpExtensionType.HTML);
    }
}
