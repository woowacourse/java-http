package org.apache.catalina.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class CookieCreatorTest {

    @Test
    void createTest() {
        String text = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=id";
        Cookie expected = new Cookie(Map.of(
                "yummy_cookie", "choco", "tasty_cookie", "strawberry",
                "JSESSIONID", "id"));

        Cookie actual = CookieCreator.create(text);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void createTest_whenCookieIsEmpty() {
        String text = "";
        Cookie expected = new Cookie(Map.of());

        Cookie actual = CookieCreator.create(text);

        assertThat(actual).isEqualTo(expected);
    }
}
