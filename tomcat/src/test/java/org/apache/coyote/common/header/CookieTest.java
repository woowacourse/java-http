package org.apache.coyote.common.header;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Cookie 는 ")
class CookieTest {

    @DisplayName("쿠키 스트링을 쿠키로 파싱해 저장한다.")
    @Test
    void parseRawCookie() {
        final String rawCookie = "user=a; host=h";
        final Cookie cookie = new Cookie(rawCookie);

        final String user = cookie.getValue("user")
                .get();
        final String host = cookie.getValue("host")
                .get();

        assertAll(
                () -> assertThat(user).isEqualTo("a"),
                () -> assertThat(host).isEqualTo("h")
        );
    }

    @DisplayName("응답 헤더에 사용될 Set-Cookie 값을 생성한다.")
    @Test
    void buildSetCookie() {
        final String actual = Cookie.setCookieBuilder("name", "value")
                .setDomain("domain.com")
                .setExpires(360)
                .setSecure()
                .asString();
        assertThat(actual).isNotNull();
    }
}
