package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void from() {
        // given
        String rawCookie = " key=value; Case-2=equ=als=; key2=value2";
        Map<String, String> expectedCookiePairs = Map.of(
            "key", "value",
            "Case-2", "equ=als=",
            "key2", "value2"
        );

        // when
        HttpCookie actual = HttpCookie.from(rawCookie);

        // then
        assertThat(actual)
            .extracting("cookieMap")
            .isEqualTo(expectedCookiePairs);
    }

}