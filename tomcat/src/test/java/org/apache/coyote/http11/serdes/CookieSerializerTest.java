package org.apache.coyote.http11.serdes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.coyote.http11.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieSerializerTest {

    @DisplayName("쿠키를 직렬화 할 수 있다")
    @Test
    void serialize() {
        String serializedText = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        Cookie cookie = Cookie.read(serializedText);
        CookieSerializer cookieSerializer = new CookieSerializer();

        assertThat(cookieSerializer.serialize(cookie)).isEqualTo(serializedText);
    }
}
