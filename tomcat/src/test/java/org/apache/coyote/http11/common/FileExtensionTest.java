package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FileExtensionTest {

    @ParameterizedTest
    @CsvSource(value = {
            "/login : false", "/login?account=gugu&password=password : false",
            "/index.html : true", "/styles.css : true", "/scripts.js : true", "/favicon.ico : true"},
            delimiter = ':'
    )
    @DisplayName("확장자가 존재하는지 판단한다.")
    void isContains(final String path, final boolean expected) {
        // when
        final boolean actual = FileExtension.isContains(path);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
