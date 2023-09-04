package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestFirstLineInfoTest {

    @Test
    void HTTP_요청의_첫_줄의_정보를_읽는다() {
        // given
        final String firstLine = "GET /index.html HTTP/1.1 ";

        // when
        HttpRequestFirstLineInfo infos = HttpRequestFirstLineInfo.from(firstLine);

        // then
        assertSoftly(softly -> {
            softly.assertThat(infos.getHttpMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(infos.getUri()).isEqualTo("/index.html");
            softly.assertThat(infos.getVersionOfTheProtocol()).isEqualTo("HTTP/1.1");
        });
    }

    @Test
    void 첫_줄이_유효하지_않으면_예외를_발생시킨다() {
        // given
        final String firstLine = "GET HTTP/1.1 ";

        // expect
        assertThatThrownBy(() -> HttpRequestFirstLineInfo.from(firstLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 Request-line 입니다.");
    }

    @Test
    void HTTP_메서드가_유효하지_않으면_예외를_발생시킨다() {
        // given
        final String firstLine = "내놔라 /index.html HTTP/1.1 ";

        // expect
        assertThatThrownBy(() -> HttpRequestFirstLineInfo.from(firstLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 HTTP 메서드입니다.");
    }
}
