package org.apache.coyote.header;

import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void 모든_요청_헤더들로_쿠키_객체를_생성한다() {
        // given
        String requestHeaders = String.join("\r\n",
                "Location: afsadfs ",
                "Accept: dfsdsadfas",
                "Cookie: JSESSIONID=abcd");

        // expect
        HttpCookie httpCookie = HttpCookie.from(requestHeaders);
    }
}
