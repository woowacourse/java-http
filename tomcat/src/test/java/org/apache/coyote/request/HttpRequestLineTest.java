package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.exception.http.InvalidStartLineException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestLineTest {

    @Nested
    class 스타트_라인_추출 {

        @Test
        void 유효한_스타트_라인이라면_스타트_라인을_추출한다() {
            final String startLine = "GET /index.html HTTP/1.1";

            final HttpRequestLine httpRequestLine = HttpRequestLine.parse(startLine);

            assertSoftly(softly -> {
                softly.assertThat(httpRequestLine.getHttpMethod()).isEqualTo(HttpMethod.GET);
                softly.assertThat(httpRequestLine.getUriPath()).isEqualTo("/index.html");
                softly.assertThat(httpRequestLine.getHttpVersion()).isEqualTo(HttpVersion.HTTP_11);
            });
        }

        @Test
        void 스타트_라인이_null이라면_예외를_던진다() {
            assertThatThrownBy(() -> HttpRequestLine.parse(null))
                    .isInstanceOf(InvalidStartLineException.class)
                    .hasMessage("Start Line is Null Or Empty");
        }

        @Test
        void 스타트_라인이_비어있으면_예외를_던진다() {
            assertThatThrownBy(() -> HttpRequestLine.parse(""))
                    .isInstanceOf(InvalidStartLineException.class)
                    .hasMessage("Start Line is Null Or Empty");
        }

        @Test
        void 스타트_라인에_유효한_정보의_개수가_3개가_아니라면_예외를_던진다() {
            final String startLine = "GET /index.html";

            assertThatThrownBy(() -> HttpRequestLine.parse(startLine))
                    .isInstanceOf(InvalidStartLineException.class)
                    .hasMessage("Start Line Element Count Not Match");
        }
    }
}
