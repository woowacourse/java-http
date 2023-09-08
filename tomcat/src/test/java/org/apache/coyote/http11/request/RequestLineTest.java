package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestLineTest {

    @Test
    void 문자열을_받아_RequestLine_객체를_생성한다() {
        // given
        final var line = "GET / HTTP/1.1";
        final var expectedMethod = "GET";
        final var expectedPath = "/";
        final var expectedProtocol = "HTTP/1.1";

        // when
        final var actual = RequestLine.from(line);

        // then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getMethod())
                    .isEqualTo(expectedMethod);
            softAssertions.assertThat(actual.getPath())
                    .isEqualTo(expectedPath);
            softAssertions.assertThat(actual.getProtocol())
                    .isEqualTo(expectedProtocol);
        });
    }
}
