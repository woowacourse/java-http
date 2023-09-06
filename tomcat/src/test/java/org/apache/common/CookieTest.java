package org.apache.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CookieTest {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String CANDY = "CANDY";

    @Test
    void 쿠키를_생성한다() {
        String cookies = JSESSIONID + "=1234-512523-6243636";
        Cookie cookie = Cookie.from(cookies);

        assertThat(cookie).isNotNull();
    }

    @Test
    void 쿠키를_조회한다() {
        String jsessionCookie = JSESSIONID + "=1234-512523-6243636";
        String joiner = "; ";
        String candyCookie = CANDY + "=LEMON";
        Cookie cookie = Cookie.from(jsessionCookie + joiner + candyCookie);

        String sessionId = cookie.getValue(JSESSIONID);
        String candy = cookie.getValue(CANDY);

        assertAll(
                () -> assertThat(sessionId).isEqualTo(jsessionCookie.split("=")[1]),
                () -> assertThat(candy).isEqualTo(candyCookie.split("=")[1])
        );
    }
}
