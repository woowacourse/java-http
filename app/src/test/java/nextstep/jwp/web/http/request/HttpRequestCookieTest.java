package nextstep.jwp.web.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.contentOf;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("쿠키와 관련된 로직을 테스트한다.")
class HttpRequestCookieTest {

    @DisplayName("헤더로 들어온 쿠키값을 Map에 저장하고 찾는 value를 찾아서 반환한다 - 성공")
    @Test
    void get_Cookie_Success() {
        // given
        String cookieKeyA = "yummy_cookie";
        String cookieValueA = "choco";

        String cookieKeyB = "tasty_cookie";
        String cookieValueB = "strawberry";

        String cookieKeyC = "JSESSIONID";
        String cookieValueC = "656cef62-e3c4-40bc-a8df-94732920ed46";

        String rawCookie =
            cookieKeyA + "=" + cookieValueA + ";" + cookieKeyB + "=" + cookieValueB + ";" + cookieKeyC + "=" + cookieValueC;

        // when
        HttpRequestCookie cookie = new HttpRequestCookie(rawCookie);

        // then
        assertThat(cookie.get(cookieKeyA)).isEqualTo(cookieValueA);
        assertThat(cookie.get(cookieKeyB)).isEqualTo(cookieValueB);
        assertThat(cookie.get(cookieKeyC)).isEqualTo(cookieValueC);
    }
}
