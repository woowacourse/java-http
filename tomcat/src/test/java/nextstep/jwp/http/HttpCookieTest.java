package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void cookie값을_받아_HttpCookie를_생성한다() {
        HttpCookie expected = new HttpCookie(Map.of("yummy_cookie", "choco",
                "tasty_cookie", "strawberry",
                "JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46"));
        HttpCookie actual = HttpCookie.from("yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 빈_HttpCookie를_생성한다() {
        HttpCookie expected = new HttpCookie(new HashMap<>());
        HttpCookie actual = HttpCookie.empty();

        assertThat(actual).isEqualTo(expected);
    }
}
