package org.apache.coyote.http11.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpMethodTest {

    @DisplayName("HttpMethod 생성")
    @ParameterizedTest(name = "value = {0}, expected = {1}")
    @CsvSource(value = {"get,GET", "post,POST"})
    void of(final String value, final HttpMethod expected) {
        // given & when
        final HttpMethod actual = HttpMethod.of(value);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("대소문자 구분 없이 HttpMethod 생성")
    @ParameterizedTest(name = "value = {0}, expected = {1}")
    @CsvSource(value = {"get,GET", "GET,GET"})
    void ofWithoutCase(final String value, final HttpMethod expected) {
        // given & when
        final HttpMethod actual = HttpMethod.of(value);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
