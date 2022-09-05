package org.apache.coyote.http11.constant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpContentTest {

    @DisplayName("확장자로 ContentType를 가져온다.")
    @ParameterizedTest
    @CsvSource(value = {"html:text/html", "css:text/css", "js:application/javascript"}, delimiter = ':')
    void extensionToContentType(String extension, String expected) {
        String actual = HttpContent.extensionToContentType(extension);

        assertThat(actual).isEqualTo(expected);
    }
}