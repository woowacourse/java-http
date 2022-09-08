package org.apache.coyote.http11.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void createCookie() {
        // given
        String cookieString = "hello=world; name=akth";
        // when
        HttpCookie cookie = HttpCookie.parse(cookieString);
        // then
        Assertions.assertAll(
                () -> assertThat(cookie.find("hello")).isEqualTo("world"),
                () -> assertThat(cookie.find("name")).isEqualTo("kth")
        );
    }

    @Test
    void createEmptyCookie() {
        // given
        String cookieString = "";
        // when
        HttpCookie cookie = HttpCookie.parse(cookieString);
        // then
        assertThat(cookie.getAsString()).isEqualTo("");
    }

    @Test
    void setCookie() {
        // given
        HttpCookie cookie = HttpCookie.parse("");
        // when
        HttpCookie responseCookie = cookie.asResponse("helloworld");
        // then
        assertThat(responseCookie.getAsString()).isEqualTo("Set-Cookie: JSESSIONID=helloworld");
    }

    @Test
    void emptyResponseCookieIfAlreadyHasSessionID() {
        // given
        String uuid = "helloworld";
        HttpCookie cookie = HttpCookie.parse(HttpCookie.SESSION_NAME + "=" + uuid);
        // when
        HttpCookie responseCookie = cookie.asResponse("helloworld");
        // then
        assertThat(responseCookie.getAsString()).isEqualTo("");
    }
}
