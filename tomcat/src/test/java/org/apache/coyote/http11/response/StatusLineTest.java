package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StatusLineTest {

    @Test
    void StatusLine_객체를_생성한다() {
        // given
        final var protocol = "HTTP/1.1";
        final var httpStatus = HttpStatus.OK;

        // when
        final var actual = StatusLine.of(protocol, httpStatus);

        // then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getProtocol())
                    .isEqualTo(protocol);
            softAssertions.assertThat(actual.getHttpStatus())
                    .isEqualTo(httpStatus);
        });
    }

    @Test
    void StatusLine_객체를_메세지로_출력한다() {
        // given
        final var protocol = "HTTP/1.1";
        final var httpStatus = HttpStatus.OK;
        final var statusLine = StatusLine.of(protocol, httpStatus);

        final var expected = "HTTP/1.1 200 OK";

        // when
        final var actual = statusLine.toString();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
