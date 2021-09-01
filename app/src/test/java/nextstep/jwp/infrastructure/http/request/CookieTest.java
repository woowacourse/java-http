package nextstep.jwp.infrastructure.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.HashMap;
import nextstep.jwp.infrastructure.http.request.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {

    @DisplayName("쿠키 파싱 테스트")
    @Test
    void of() {
        final HashMap<String, String> expectedMap = new HashMap<>();
        expectedMap.put("yummy_cookie", "choco");
        expectedMap.put("tasty_cookie", "");
        expectedMap.put("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46");
        final Cookie expected = new Cookie(expectedMap);
        final Cookie actual = Cookie.of("yummy_cookie=choco; tasty_cookie=; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("잘못된 쿠키 형식이면 예외 처리")
    @Test
    void invalidCookie() {
        String value = "yummy_cookie=choco; =strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        assertThatIllegalArgumentException().isThrownBy(() -> Cookie.of(value));
    }
}