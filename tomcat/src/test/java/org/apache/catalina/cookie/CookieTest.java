package org.apache.catalina.cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    void ofJSessionIdTest() {
        Cookie expected = new Cookie(Map.of("JSESSIONID", "id"));

        Cookie actual = Cookie.ofJSessionId("id");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void addTest() {
        Cookie cookie1 = new Cookie(Map.of("a", "a"));
        Cookie cookie2 = new Cookie(Map.of("b", "b"));
        Cookie expected = new Cookie(Map.of("a", "a", "b", "b"));

        cookie1.add(cookie2);

        assertThat(cookie1).isEqualTo(expected);
    }

    @Test
    void getTest() {
        Cookie cookie = new Cookie(Map.of("a", "a"));

        assertAll(
                () -> assertThat(cookie.get("a")).isEqualTo("a"),
                () -> assertThat(cookie.get("not-exist")).isNull()
        );
    }

    @Test
    void getSessionIdTest_whenSessionIdExist() {
        Cookie cookie = new Cookie(Map.of("JSESSIONID", "id"));
        String expected = "id";

        String actual = cookie.getSessionId();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getSessionIdTest_whenSessionIdNotExist() {
        Cookie cookie = new Cookie();

        String actual = cookie.getSessionId();

        assertThat(actual).isNull();
    }
}
