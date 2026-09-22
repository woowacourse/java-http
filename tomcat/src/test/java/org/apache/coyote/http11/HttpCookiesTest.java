package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HTTP Cookie 헤더 파싱")
class HttpCookiesTest {

    @Test
    @DisplayName("세미콜론으로 구분된 쿠키에서 JSESSIONID를 찾는다")
    void findsJSessionId() {
        // given
        final var headerValue = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=session-id";

        // when
        final var cookies = HttpCookies.parse(headerValue);

        // then
        assertThat(cookies.get("JSESSIONID")).contains("session-id");
    }

    @Test
    @DisplayName("쿠키 값에 포함된 등호를 보존한다")
    void preservesEqualsSignInValue() {
        // given
        final var headerValue = "token=header.payload=signature";

        // when
        final var cookies = HttpCookies.parse(headerValue);

        // then
        assertThat(cookies.get("token")).contains("header.payload=signature");
    }

    @Test
    @DisplayName("이름과 값으로 구성되지 않은 쿠키는 무시한다")
    void ignoresMalformedCookie() {
        // given
        final var headerValue = "broken; JSESSIONID=session-id";

        // when
        final var cookies = HttpCookies.parse(headerValue);

        // then
        assertThat(cookies.get("broken")).isEmpty();
    }
}
