package nextstep.jwp.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpCookieTest {

    @DisplayName("쿠키 생성을 테스트 한다.")
    @Test
    void createCookie() {
        //given
        String cookies = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        //when
        HttpCookie httpCookie = HttpCookie.from(cookies);
        //then
        assertThat(httpCookie.size()).isEqualTo(3);
    }

    @DisplayName("JSESSIONID 를 반환하는지 테스트한다.")
    @Test
    void getSessionId() {
        //given
        String cookies = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        //when
        HttpCookie httpCookie = HttpCookie.from(cookies);
        //then
        assertThat(httpCookie.getSessionId()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}