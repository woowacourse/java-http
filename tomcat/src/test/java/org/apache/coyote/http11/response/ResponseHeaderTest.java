package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ResponseHeaderTest {

    @Test
    void 비어있는_ResponseHeader_객체를_생성한다() {
        // given & when
        final var actual = ResponseHeader.createEmpty();

        // then
        assertThat(actual.getHeaders()).isEmpty();
    }

    @Test
    void 특정_header를_추가한다() {
        // given
        final var key = "Content-Type";
        final var value = "text/html";
        final var responseHeader = ResponseHeader.createEmpty();

        // when
        responseHeader.addHeader(key, value);

        // then
        assertSoftly(softAssertions -> {
            final var headers = responseHeader.getHeaders();
            softAssertions.assertThat(headers.containsKey(key))
                    .isTrue();
            softAssertions.assertThat(headers.get(key))
                    .isEqualTo(value);
        });
    }

    @Test
    void ResponseHeader_객체를_메세지로_출력한다() {
        // given
        final var responseHeader = ResponseHeader.createEmpty();
        responseHeader.addHeader("Content-Type", "text/html");
        responseHeader.addHeader("Content-Length", "80");

        final var expected = String.join("\r\n",
                "Content-Type: text/html",
                "Content-Length: 80");

        // when
        final var actual = responseHeader.toString();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
