package nextstep.jwp.http.message;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookiesTest {

    @DisplayName("쿠키 이름을 통해 쿠키를 찾을 수 있다.")
    @Test
    void findByName() {
        // given
        HttpCookie httpCookie1 = new HttpCookie("name1", "value1");
        HttpCookie httpCookie2 = new HttpCookie("name2", "value2");
        HttpCookies httpCookies = new HttpCookies(httpCookie1, httpCookie2);

        // when
        HttpCookie findHttpCookie1 = httpCookies.findByName(httpCookie1.getName()).get();
        HttpCookie findHttpCookie2 = httpCookies.findByName(httpCookie2.getName()).get();

        // then
        assertThat(httpCookie1).isSameAs(findHttpCookie1);
        assertThat(httpCookie2).isSameAs(findHttpCookie2);
    }

    @DisplayName("쿠키 이름을 통해 쿠키를 삭제할 수 있다.")
    @Test
    void removeCookieByName() {
        // given
        HttpCookie httpCookie1 = new HttpCookie("name1", "value1");
        HttpCookie httpCookie2 = new HttpCookie("name2", "value2");
        HttpCookies httpCookies = new HttpCookies(httpCookie1, httpCookie2);

        // when
        httpCookies.removeCookieByName(httpCookie1.getName());

        // then
        assertThat(httpCookies.getCookies()).hasSize(1);
    }

    @DisplayName("요청으로 들어온 문자열 형식의 쿠키들을 변환할 수 있다.")
    @Test
    void parseFrom_문자열() {
        // given
        String cookies = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        HttpCookies parsedCookies = HttpCookies.parseFrom(cookies);

        // then
        assertThat(parsedCookies.getCookies()).hasSize(3);
        assertThat(parsedCookies.getCookies())
            .containsExactly(
                new HttpCookie("yummy_cookie", "choco"),
                new HttpCookie("tasty_cookie", "strawberry"),
                new HttpCookie("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46")
            );
    }

    @DisplayName("여러 개의 쿠키의 값을 문자열로 변환할 수 있다.")
    @Test
    void asString_쿠키_문자열_변환() {
        // given
        String cookies = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        HttpCookies parsedCookies = HttpCookies.parseFrom(cookies);

        // then
        assertThat(parsedCookies.asString())
            .isEqualTo(cookies);
    }
}
