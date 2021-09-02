package nextstep.jwp.http.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void createHttpToken() {
        Map<String, String> map = new HashMap<>();
        map.put("yummy_cookie", "choco");
        map.put("tasty_cookie", "strawberry");
        map.put("JSESSIONID", "656cef62");

        HttpCookie expected = new HttpCookie(map);
        HttpCookie actual = HttpCookie.of("yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62");

        assertThat(actual).isEqualTo(expected);
    }
}
