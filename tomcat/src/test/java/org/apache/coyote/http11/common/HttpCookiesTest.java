package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookiesTest {

    @Test
    @DisplayName("쿠키 정보를 저장할 수 있다.")
    void createHttpCookies() {
        //given
        String cookieInformation = "JSESSIONID=testJSessionId";

        //when
        HttpCookies httpCookies = HttpCookies.from(cookieInformation);

        //then
        Cookie jSessionCookie = httpCookies.get("JSESSIONID");
        assertThat(jSessionCookie.getValue()).isEqualTo("testJSessionId");
    }

    @Test
    @DisplayName("쿠키의 속성 값과 함께 쿠키를 저장할 수 있다.")
    void createHttpCookiesWithAttribute() {
        //given
        String cookieInformation = "JSESSIONID=testJSessionId; charset=UTF-8";

        //when
        HttpCookies httpCookies = HttpCookies.from(cookieInformation);

        //then
        Cookie jSessionCookie = httpCookies.get("JSESSIONID");
        assertThat(jSessionCookie.getValue()).isEqualTo("testJSessionId");
        assertThat(jSessionCookie.getAttribute()).isEqualTo("charset=UTF-8");
    }

    @Test
    @DisplayName("여러 개의 쿠키를 저장할 수 있다.")
    void createSeveralHttpCookies() {
        //given
        String cookieInformation = "JSESSIONID=testJSessionId; charset=UTF-8, TESTCOOKIE=NewJeans";

        //when
        HttpCookies httpCookies = HttpCookies.from(cookieInformation);

        //then
        List<Cookie> cookies = List.of(httpCookies.get("JSESSIONID"), httpCookies.get("TESTCOOKIE"));

        assertThat(cookies).extractingResultOf("getValue")
                .containsExactlyInAnyOrder("testJSessionId", "NewJeans");
        assertThat(cookies).extractingResultOf("getAttribute")
                .containsExactlyInAnyOrder("charset=UTF-8", "");
    }

}