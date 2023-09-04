package org.apache.coyote.httprequest;

import org.apache.coyote.httprequest.header.RequestHeaderType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class RequestHeadersTypeTest {

    @Test
    void 잘못된_이름의_헤더로_요청을_보내면_지원하지_않는_헤더_요소가_반환된다() {
        // given
        final String invalidHeaderName = "BEBE";

        // when
        final RequestHeaderType unsupportedHeaderType = RequestHeaderType.from(invalidHeaderName);

        // then
        assertThat(unsupportedHeaderType).isEqualTo(RequestHeaderType.UNSUPPORTED_HEADER);
    }
}
