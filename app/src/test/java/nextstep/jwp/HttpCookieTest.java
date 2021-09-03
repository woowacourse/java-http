package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.http.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpCookieTest {

    @DisplayName("header로 들어온 쿠키의 parameter가 제대로 등록되는지 확인")
    @Test
    void cookieParameter() {
        String line = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=1234";
        HttpCookie cookie = new HttpCookie(line);

        assertThat(cookie.getAttribute("JSESSIONID")).isEqualTo("1234");
        assertThat(cookie.getAttribute("yummy_cookie")).isEqualTo("choco");
    }
}
