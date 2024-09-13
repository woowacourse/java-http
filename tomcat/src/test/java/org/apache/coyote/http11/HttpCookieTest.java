package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    @DisplayName("이름으로 쿠키 값을 반환한다.")
    void getCookie () {
        List<String> cookieNames = List.of("yummy_cookie", "tasty_cookie", "JSESSIONID");
        HttpCookie httpCookie = new HttpCookie(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );

        List<String> actual = List.of("choco", "strawberry", "656cef62-e3c4-40bc-a8df-94732920ed46");
        List<String> expected = new ArrayList<>();
        for (String name : cookieNames) {
            expected.add(httpCookie.getCookie(name));
        }

        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("유효하지 않은 이름은 쿠키 값을 빈문자열로 반환한다.")
    void getCookieWhenInvalidName() {
        HttpCookie httpCookie = new HttpCookie("cookie=chock");

        String invalidCookie = httpCookie.getCookie("coookie");

        assertThat(invalidCookie).isEmpty();
    }
}
