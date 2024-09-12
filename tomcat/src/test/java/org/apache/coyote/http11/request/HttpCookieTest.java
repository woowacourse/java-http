package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("쿠키 정보를 올바르게 파싱해서 생성하고, 가져올 수 있다.")
    @Test
    void get() {
        // given
        String cookie = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        HttpCookie httpCookie = new HttpCookie(cookie);

        // when
        String yummyCookie = httpCookie.get("yummy_cookie");
        String tastyCookie = httpCookie.get("tasty_cookie");
        String JSESSIONID = httpCookie.get("JSESSIONID");

        // then
        assertAll(
                () -> assertThat(yummyCookie).isEqualTo("choco"),
                () -> assertThat(tastyCookie).isEqualTo("strawberry"),
                () -> assertThat(JSESSIONID).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }
}
