package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.common.HttpVersion;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpResponseTest {

    @Test
    void 헤더를_추가한다() {
        // given
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        httpResponse.addHeader("HEADER", "HI");

        // then
        assertThat(httpResponse.getHeaders().get("HEADER")).isEqualTo("HI");
    }

    @Test
    void httpStatus를_설정한다() {
        // given
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        httpResponse.setHttpStatus(HttpStatus.OK);

        // then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void 리다이렉트_url을_설정한다() {
        // given
        final HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        httpResponse.sendRedirect("hello.html");

        // then
        assertThat(httpResponse.getRedirect()).isEqualTo("hello.html");
    }
}
