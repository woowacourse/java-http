package nextstep.jwp.server.http;

import nextstep.jwp.server.http.common.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class HttpCookieTest {

    @Test
    @DisplayName("여러 개의 쿠키가 들어오면 파싱해서 저장하며 생성된다.")
    void createCookie() {
        // given
        String cookies = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        HttpCookie httpCookie = new HttpCookie(cookies);

        // when
        // then
        assertThat(httpCookie.getCookie()).contains(
                entry("yummy_cookie", "choco"), entry("tasty_cookie", "strawberry"),
                        entry("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46"));
    }

    @Test
    @DisplayName("쿠키에 여러 개를 추가하고 string으로 변환한다.")
    void convertToString() {
        // given
        HttpCookie httpCookie = new HttpCookie();

        // when
        httpCookie.addCookie("yummy_cookie", "choco");
        httpCookie.addCookie("tasty_cookie", "strawberry");
        httpCookie.addCookie("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46");

        // then
        assertThat(httpCookie.convertString()).isEqualTo("yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}
