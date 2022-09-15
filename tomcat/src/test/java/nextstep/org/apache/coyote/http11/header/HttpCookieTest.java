package nextstep.org.apache.coyote.http11.header;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.header.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("쿠키를 생성한다")
    @Test
    void createCookie() {
        final String requestCookie = "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        final HttpCookie actual = HttpCookie.from(requestCookie);

        assertThat(actual.isExist()).isTrue();
        assertThat(actual.getSessionId()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}
