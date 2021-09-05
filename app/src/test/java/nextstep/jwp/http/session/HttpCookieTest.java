package nextstep.jwp.http.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("raw한 쿠키값을 쿠키에 저장한다.")
    @Test
    public void createCookie() {
        String cookieKey1 = "crew";
        String cookieValue1 = "choonsik";
        String cookieKey2 = "JSESSIONID";
        String cookieValue2 = "77f5aa18-cf28-4541-b492-5d0a58813afa";

        String rawCookie =
            cookieKey1 + "=" + cookieValue1 + ";" + cookieKey2 + "=" + cookieValue2 + ";";

        HttpCookie cookie = new HttpCookie(rawCookie);

        assertThat(cookie.containSession()).isTrue();
        assertThat(cookie.getSessionId()).isEqualTo(cookieValue2);
    }

    @DisplayName("쿠키는 비어있을 수 있다.")
    @Test
    public void emptyCookie() {
        String rawCookie = "";
        HttpCookie cookie = new HttpCookie(rawCookie);

        assertThat(cookie.containSession()).isFalse();
        assertThat(cookie.getSessionId()).isEmpty();
    }

}