package org.apache.catalina.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class CookieUtilsTest {

    @Test
    void createCookieTest() {
        String text = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=id";
        Cookie expected = new Cookie(Map.of(
                "yummy_cookie", "choco", "tasty_cookie", "strawberry",
                "JSESSIONID", "id"));

        Cookie actual = CookieUtils.createCookie(text);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createCookieTest_whenCookieIsEmpty() {
        String text = "";
        Cookie expected = new Cookie(Map.of());

        Cookie actual = CookieUtils.createCookie(text);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void toValuesTest() {
        Cookie cookie = new Cookie(Map.of(
                "yummy_cookie", "choco", "tasty_cookie", "strawberry",
                "JSESSIONID", "id"));
        List<String> expected = List.of(
          "yummy_cookie=choco", "tasty_cookie=strawberry", "JSESSIONID=id", "; ");

        String actual = CookieUtils.toValues(cookie);

        assertThat(actual).contains(expected);
    }

    @Test
    void toValueTest_whenCookieIsEmpty() {
        Cookie cookie = new Cookie();
        String expected = "";

        String actual = CookieUtils.toValues(cookie);

        assertThat(actual).isEqualTo(expected);
    }
}
