package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ContentTypeTest {

    @ParameterizedTest
    @CsvSource(delimiter = ':', value = {"style.js:JAVASCRIPT", "style.css:CSS", "index.html:HTML", "login:HTML"})
    void Content_Type을_찾는다(String type, ContentType contentType) {
        // given
        ContentType actual = ContentType.findContentType(type);

        // when & then
        assertThat(actual).isEqualTo(contentType);
    }

}
