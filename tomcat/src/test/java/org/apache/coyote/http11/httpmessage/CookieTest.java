package org.apache.coyote.http11.httpmessage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    void cookie를_생성할_수_있다() {
        // given
        final String key = "JSESSIONID";
        final String value = "1234";

        // when
        Cookie cookie = new Cookie(Map.of(key, value));

        // then
        assertThat(cookie.getCookies()).isEqualTo(Map.of(key, value));
    }

    @Test
    void cookie를_출력할_수_있다() {
        // given
        Map<String, String> cookies = Map.of("JSESSIONID", "1234", "name", "park");

        // when
        Cookie cookie = new Cookie(cookies);

        // then
        assertThat(cookie.toString()).isEqualTo("JSESSIONID=1234; name=park ");
    }
}
