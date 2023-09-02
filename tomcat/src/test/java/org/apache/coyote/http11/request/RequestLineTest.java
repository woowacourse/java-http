package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpVersion;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class RequestLineTest {

    @Test
    void RequestLine을_생성한다() {
        // given
        String requestLine = "GET /index.html HTTP/1.1";

        // when
        RequestLine line = RequestLine.parse(requestLine);

        // then
        assertSoftly(softly -> {
            softly.assertThat(line.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(line.getRequestUri()).isEqualTo("/index.html");
            softly.assertThat(line.getHttpVersion()).isEqualTo(HttpVersion.HTTP_1_1);
        });
    }

    @Test
    void 쿼리_파라미터를_반환한다() {
        // given
        String requestLine = "GET /login?account=boxster&password=password HTTP/1.1";

        // when
        RequestLine line = RequestLine.parse(requestLine);

        // then
        assertSoftly(softly -> {
            softly.assertThat(line.getQueryParam("account")).isEqualTo("boxster");
            softly.assertThat(line.getQueryParam("password")).isEqualTo("password");
        });
    }

    @Test
    void 쿼리_파라미터가_없으면_빈_문자열을_반환한다() {
        // given
        String requestLine = "GET /login HTTP/1.1";

        // when
        RequestLine line = RequestLine.parse(requestLine);

        // then
        assertThat(line.getQueryParam("account")).isEmpty();
    }
}
