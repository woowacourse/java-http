package org.apache.coyote.httprequest;

import org.apache.coyote.common.HttpVersion;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SuppressWarnings("NonAsciiCharacters")
class HttpRequestLineTest {

    @Test
    void 생성에_성공한다() {
        // given
        final String requestMethodInput = "POST";
        final String requestUrlInput = "/register";
        final String httpVersionInput = "HTTP/1.1";
        final String requestInput = String.join(" ", requestMethodInput, requestUrlInput, httpVersionInput);

        // when
        final HttpRequestLine httpRequestLine = HttpRequestLine.from(requestInput);

        // then
        assertSoftly(softly -> {
            assertThat(httpRequestLine.getRequestMethod()).isEqualTo(RequestMethod.from(requestMethodInput));
            assertThat(httpRequestLine.getPath()).isEqualTo(RequestUri.from(requestUrlInput).getPath());
            assertThat(httpRequestLine.getHttpVersion()).isEqualTo(HttpVersion.from(httpVersionInput));
        });
    }
}
