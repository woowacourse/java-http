package org.apache.coyote.http11.request.spec;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import org.apache.coyote.http11.general.HttpHeaders;
import org.apache.coyote.http11.session.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    @DisplayName("응답 헤더에 쿠키를 설정한다")
    void addCookie() {
        HttpHeaders httpHeaders = new HttpHeaders(new HashMap<>());

        Cookie cookie = new Cookie("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46");
        httpHeaders.addCookie(cookie);

        assertThat(httpHeaders.get("Set-Cookie")).isEqualTo("JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
    }

}
