package org.apache.coyote.httpresponse;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class CookieResponseHeaderTest {

    @Test
    void jSessionId로_쿠키_값을_세팅한다() {
        // given
        final String jSessionId = "bebe";

        // when
        final CookieResponseHeader cookieResponseHeader = CookieResponseHeader.createByJSessionId(jSessionId);
        final String actual = cookieResponseHeader.getFormattedValue();
        final String expected = "Set-Cookie: JSESSIONID=bebe";

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
