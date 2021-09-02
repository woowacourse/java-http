package nextstep.jwp.httpmessage;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HttpCookie 테스트")
class HttpCookieTest {

    @Test
    void parseToMap() {
        //given
        final String expect = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        //when
        final HttpCookie httpCookie = new HttpCookie(expect);
        //then
        Assertions.assertThat(httpCookie.getCookie("yummy_cookie")).isEqualTo("choco");
    }

    @Test
    void parseHeaderToCookie() {
        //given
        final HttpCookie httpCookie = new HttpCookie();
        //when
        httpCookie.setCookie("tasty_cookie", "strawberry");
        httpCookie.setCookie("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46");
        //then
        Assertions.assertThat(httpCookie.toValuesString()).isEqualTo("tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}
