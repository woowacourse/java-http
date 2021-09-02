package nextstep.jwp.framework.http.session;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("쿠키를 추가한다.")
    @Test
    void add() {
        //given
        HttpCookie httpCookie = new HttpCookie();

        //when
        httpCookie.addCookies("wilder=nice_guy; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46; pk=thank_you");

        //then
        assertThat(httpCookie.get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @DisplayName("쿠키를 출력한다.")
    @Test
    void print() {
        //given
        HttpCookie httpCookie = new HttpCookie();

        //when
        httpCookie.addCookies("wilder=nice_guy; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46; pk=thank_you");

        //then
        assertThat(httpCookie.cookies()).isEqualTo("JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46; " + "\r\n");
    }
}
