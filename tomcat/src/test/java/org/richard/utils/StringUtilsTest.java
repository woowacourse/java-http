package org.richard.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @DisplayName("StringUtils의 isNullOrBlank 메서드는")
    @Nested
    class isNullOrBlankTest {
        @DisplayName("매개변수 하나 전달시, null 또는 빈 문자열은 true를 반환한다")
        @Test
        void return_true_on_null_or_empty_string() {
            // given
            final String nullString = null;
            final String emptyString = "  ";

            // when
            final boolean nullStringResult = StringUtils.isNullOrBlank(nullString);
            final boolean emptyStringResult = StringUtils.isNullOrBlank(emptyString);

            // then
            assertAll(
                    () -> assertThat(nullStringResult).isTrue(),
                    () -> assertThat(emptyStringResult).isTrue()
            );
        }

        @DisplayName("매개변수 여러 개 전달 시, 하나라도 null 또는 빈 문자열이 있다면 true를 반환한다")
        @Test
        void return_true_on_null_or_empty_strings() {
            // given
            final String nullString = null;
            final String emptyString = "  ";

            // when
            final boolean actual = StringUtils.isNullOrBlank(nullString, emptyString);

            // then
            assertThat(actual).isTrue();
        }

        @DisplayName("매개변수 하나 전달 시, 공백이 아닌 문자가 하나라도 있다면 false를 반환한다")
        @Test
        void return_false_on_string_contains_any_character_without_white_space() {
            // given
            final String validString = " character ";

            // when
            final boolean actual = StringUtils.isNullOrBlank(validString);

            // then
            assertThat(actual).isFalse();
        }

        @DisplayName("매개변수 여러 개 전달 시, 모두 공백이 아닌 문자가 하나라도 있어야 false를 반환한다")
        @Test
        void return_false_on_strings_contains_any_character_without_white_space() {
            // given
            final String validString = " character ";
            final String secondValidString = " second ";

            // when
            final boolean actual = StringUtils.isNullOrBlank(validString, secondValidString);

            // then
            assertThat(actual).isFalse();
        }
    }
}
