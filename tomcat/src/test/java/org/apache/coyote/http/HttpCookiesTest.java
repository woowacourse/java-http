package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

public class HttpCookiesTest {

    @Test
    void 쿠키라인에서_쿠키들을_파싱할_수_있다() {
        // given
        String cookieLine = "username=JohnDoe; sessionId=abc123";

        // when
        HttpCookies cookies = new HttpCookies(cookieLine);

        // then
        assertAll(
                () -> assertThat(cookies.get("username").getValue()).isEqualTo("JohnDoe"),
                () -> assertThat(cookies.get("sessionId").getValue()).isEqualTo("abc123")
        );
    }

    @Test
    void 쿠키라인이_없으면_빈_맵을_생성한다() {
        // given
        String cookieLine = null;

        // when
        HttpCookies cookies = new HttpCookies(cookieLine);

        // then
        assertThat(cookies.get("username")).isNull();
    }

    @Test
    void 첫번째_이퀄에서만_분리된다() {
        // given
        String cookieLine = "name=value=extra";

        // when
        HttpCookies cookies = new HttpCookies(cookieLine);

        // then
        assertThat(cookies.get("name").getValue()).isEqualTo("value=extra");
    }

    @Test
    void 새로운_쿠키를_추가할_수_있다() {
        // given
        HttpCookies cookies = new HttpCookies(null);
        HttpCookie newCookie = new HttpCookie("userToken", "xyz789");

        // when
        cookies.add(newCookie);

        // then
        assertThat(cookies.get("userToken").getValue()).isEqualTo("xyz789");
    }

    @Test
    void 잘못된_형식의_쿠키라인이_있으면_무시한다() {
        // given
        String cookieLine = "usernameJohnDoe";

        // when
        HttpCookies cookies = new HttpCookies(cookieLine);

        // then
        assertThat(cookies.get("username")).isNull();
    }

    @Test
    void 여러개의_쿠키를_추가하고_조회할_수_있다() {
        // given
        HttpCookies cookies = new HttpCookies(null);
        HttpCookie cookie1 = new HttpCookie("username", "JaneDoe");
        HttpCookie cookie2 = new HttpCookie("sessionId", "xyz456");

        // when
        cookies.add(cookie1);
        cookies.add(cookie2);

        // then
        assertAll(
                () -> assertThat(cookies.get("username").getValue()).isEqualTo("JaneDoe"),
                () -> assertThat(cookies.get("sessionId").getValue()).isEqualTo("xyz456")
        );
    }
}
