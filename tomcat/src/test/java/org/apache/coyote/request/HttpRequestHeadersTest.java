package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.exception.http.InvalidHeaderException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestHeadersTest {

    @Nested
    class 헤더_추출 {

        @Test
        void 헤더값이_정상이라면_키_값_형식에_알맞은_헤더_정보를_반환한다() {
            final List<String> lines = new ArrayList<>(
                    List.of(
                            "Content-Type: text/html",
                            "Content-Length: 12"
                    )
            );

            final HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.parse(lines);

            assertSoftly(softly -> {
                softly.assertThat(httpRequestHeaders.getHeader("Content-Type")).isEqualTo("text/html");
                softly.assertThat(httpRequestHeaders.getHeader("Content-Length")).isEqualTo("12");
            });
        }

        @Test
        void 헤더값이_존재하지_않는_헤더가_있으면_예외를_던진다() {
            final List<String> lines = new ArrayList<>(
                    List.of(
                            "Content-Type: text/html",
                            "Content-Length: "
                    )
            );

            assertThatThrownBy(() -> HttpRequestHeaders.parse(lines))
                    .isInstanceOf(InvalidHeaderException.class)
                    .hasMessage("Header Element Count Not Match");
        }
    }
}
