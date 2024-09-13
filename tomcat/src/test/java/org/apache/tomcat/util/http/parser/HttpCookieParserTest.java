package org.apache.tomcat.util.http.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieParserTest {

    @DisplayName("HttpCookie를 파싱할 수 있다.")
    @Test
    void parse() {
        String cookie = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        Map<String, String> actual = HttpCookieParser.parseCookies(cookie);
        Map<String, String> expected = Map.of("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46",
                "yummy_cookie", "choco", "tasty_cookie", "strawberry");

        assertThat(actual).isEqualTo(expected);
    }
}
