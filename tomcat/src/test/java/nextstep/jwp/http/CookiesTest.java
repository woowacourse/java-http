package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.Cookie;
import org.apache.http.Cookies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookiesTest {

    @Test
    @DisplayName("쿠키를 추출할 수 있다.")
    void parse() {
        List<Cookie> cookies = new ArrayList<>();
        cookies.add(new Cookie("yummy_cookie", "choco"));
        cookies.add(new Cookie("tasty_cookie", "strawberry"));
        cookies.add(new Cookie("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46"));

        Cookies actual = Cookies.parse(
            "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        Cookies expected = new Cookies(cookies);
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("JSESSIONID가 있으면 해당하는 값을 반환한다.")
    void getJSessionId() {
        Cookies cookies = Cookies.parse("JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        String actual = cookies.findJSessionId().orElseThrow();

        String expected = "656cef62-e3c4-40bc-a8df-94732920ed46";
        assertThat(actual).isEqualTo(expected);
    }
}
