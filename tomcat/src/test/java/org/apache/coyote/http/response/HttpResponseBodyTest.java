package org.apache.coyote.http.response;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseBodyTest {

    @Test
    @DisplayName("문자열로 ResponseBody 생성")
    void createFromString() {
        // given
        final String content = "Hello World!";

        // when
        final HttpResponseBody body = HttpResponseBody.from(content);

        // then
        assertSoftly(softly -> {
            softly.assertThat(body.toString()).isEqualTo(content);
            softly.assertThat(body.getContentLength()).isEqualTo(content.getBytes().length);
        });
    }

    @Test
    @DisplayName("빈 ResponseBody 생성")
    void createEmpty() {
        // when
        final HttpResponseBody body = HttpResponseBody.empty();

        // then
        assertSoftly(softly -> {
            softly.assertThat(body.toString()).isEmpty();
            softly.assertThat(body.getContentLength()).isEqualTo(0);
        });
    }

    @Test
    @DisplayName("한글 내용으로 ResponseBody 생성")
    void createWithKoreanContent() {
        // given
        final String koreanContent = "안녕하세요!";

        // when
        final HttpResponseBody body = HttpResponseBody.from(koreanContent);

        // then
        assertSoftly(softly -> {
            softly.assertThat(body.toString()).isEqualTo(koreanContent);
            softly.assertThat(body.getContentLength()).isEqualTo(koreanContent.getBytes().length);
        });
    }

    @Test
    @DisplayName("큰 내용으로 ResponseBody 생성")
    void createWithLargeContent() {
        // given
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("테스트 내용 ");
        }
        final String largeContent = sb.toString();

        // when
        final HttpResponseBody body = HttpResponseBody.from(largeContent);

        // then
        assertSoftly(softly -> {
            softly.assertThat(body.toString()).isEqualTo(largeContent);
            softly.assertThat(body.getContentLength()).isEqualTo(largeContent.getBytes().length);
        });
    }
}
