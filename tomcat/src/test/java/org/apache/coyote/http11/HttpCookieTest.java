package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    @DisplayName("쿠키 값을 파싱해서 map으로 저장한다.")
    void parse() {
        //given
        final var expected = Map.of(
                "jmt_cookie", "chicken",
                "맛꿀마_cookie", "pizza",
                "JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46"
        );

        //when
        final var httpCookie = HttpCookie.parse(
                "jmt_cookie=chicken; 맛꿀마_cookie=pizza; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        //then
        assertThat(httpCookie.getHeader()).isEqualTo(expected);
    }
}
