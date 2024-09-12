package org.apache.coyote.http11.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    @DisplayName("HttpCookie 객체를 생성한다.")
    void createTest() {
        // given
        List<String> cookieField = new ArrayList<>(List.of("JSESSIONID=1234", "path=/", "HttpOnly"));

        // when
        HttpCookie cookie = HttpCookie.from(cookieField);

        // then
        assertEquals("1234", cookie.getJsessionid());
    }

    @Test
    @DisplayName("HttpCookie 객체를 생성한다.")
    void stringifyTest() {
        // given
        Map<String, String> directives = Map.of("JSESSIONID", "1234");
        HttpCookie cookie = new HttpCookie(directives, true);

        // when
        String cookieString = cookie.stringify();

        // then
        assertEquals("JSESSIONID=1234; HttpOnly", cookieString);
    }
}
