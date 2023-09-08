package org.apache.coyote.http11.response;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ContentTypeTest {

    @Test
    void ContentType_변환_테스트() {
        String content = "index.html";

        ContentType contentType = ContentType.from(content);

        Assertions.assertThat(contentType).isEqualTo(ContentType.HTML);
    }
}
