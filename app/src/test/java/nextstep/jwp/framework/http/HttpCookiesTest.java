package nextstep.jwp.framework.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class HttpCookiesTest {

    @Test
    @DisplayName("Cookie 문자열을 HttpCookies 객체로 변환 테스트")
    void stringParsingTest() {

        // given
        String cookies = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        final HttpCookies httpCookies = HttpCookies.from(cookies);

        // then
        final HttpCookies expected = new HttpCookies().addCookie("yummy_cookie", "choco")
                                                      .addCookie("tasty_cookie", "strawberry")
                                                      .addCookie("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46");

        assertThat(httpCookies).usingRecursiveComparison().isEqualTo(expected);
    }
}
