package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestHeaderTest {

    @Test
    void 문자열_리스트를_받아_RequestHeader_객체를_생성한다() {
        // given
        final var line1 = "HOST: localhost:8080";
        final var line2 = "Connection-Length: 59";
        final var line3 = "Content-Type: application/x-www-form-urlencoded";
        final var line4 = "Accept: */*";
        final var lines = List.of(line1, line2, line3, line4);

        final var expectedHost = "localhost:8080";
        final var expectedConnectionLength = "59";
        final var expectedContentType = "application/x-www-form-urlencoded";
        final var expectedAccept = "*/*";

        // when
        final var actual = RequestHeader.from(lines);

        // then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getValue("HOST"))
                    .isEqualTo(expectedHost);
            softAssertions.assertThat(actual.getValue("Connection-Length"))
                    .isEqualTo(expectedConnectionLength);
            softAssertions.assertThat(actual.getValue("Content-Type"))
                    .isEqualTo(expectedContentType);
            softAssertions.assertThat(actual.getValue("Accept"))
                    .isEqualTo(expectedAccept);
        });
    }

    @Nested
    class isNotFormContentType_테스트 {

        @Test
        void Content_Type이_Form_형식이_아니면_true를_반환한다() {
            // given
            final var line1 = "HOST: localhost:8080";
            final var line2 = "Connection-Length: 59";
            final var line3 = "Content-Type: text/html";
            final var line4 = "Accept: */*";
            final var lines = List.of(line1, line2, line3, line4);
            final var requestHeader = RequestHeader.from(lines);

            // when
            final var actual = requestHeader.isNotFormContentType();

            // then
            assertThat(actual).isTrue();
        }

        @Test
        void Content_Type이_Form_형식이면_false를_반환한다() {
            // given
            final var line1 = "HOST: localhost:8080";
            final var line2 = "Connection-Length: 59";
            final var line3 = "Content-Type: application/x-www-form-urlencoded";
            final var line4 = "Accept: */*";
            final var lines = List.of(line1, line2, line3, line4);
            final var requestHeader = RequestHeader.from(lines);

            // when
            final var actual = requestHeader.isNotFormContentType();

            // then
            assertThat(actual).isFalse();
        }
    }

    @Nested
    class getValue_테스트 {

        @Test
        void key를_통해_해당하는_header_값을_조회한다() {
            // given
            final var lines = Collections.singletonList("HOST: localhost:8080");
            final var requestHeader = RequestHeader.from(lines);
            final var key = "HOST";
            final var expected = "localhost:8080";

            // when
            final var actual = requestHeader.getValue(key);

            // then
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        void key가_존재하지_않으면_null_값을_반환한다() {
            // given
            final var lines = Collections.singletonList("HOST: localhost:8080");
            final var requestHeader = RequestHeader.from(lines);
            final var key = "Content-Type";

            // when
            final var actual = requestHeader.getValue(key);

            // then
            assertThat(actual).isNull();
        }
    }
}
