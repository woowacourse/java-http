package nextstep.jwp.request;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.constants.HttpTerms;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpTermsCookieTest {

    @Test
    @DisplayName("쿠키로 들어온 내용을 파싱한다.")
    void parseCookie() {
        final String sessionValue = "656cef62-e3c4-40bc-a8df-94732920ed46";
        final String cookieValue = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=" + sessionValue;
        HttpCookie httpCookie = new HttpCookie(cookieValue);

        assertThat(httpCookie.get(HttpTerms.JSESSIONID)).isEqualTo(sessionValue);
    }
}