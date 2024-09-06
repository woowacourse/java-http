package org.apache.coyote.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void HttpCookie를_조립한다() {
        // given
        HttpCookie httpCookie = HttpCookie.create();
        httpCookie.setJsessionid("656cef62-e3c4-40bc-a8df-94732920ed46");

        // when
        StringBuilder builder = new StringBuilder();
        httpCookie.assemble(builder);

        // then
        String expected = "Set-Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 \r\n";
        assertThat(builder.toString()).contains(expected);
    }
}
