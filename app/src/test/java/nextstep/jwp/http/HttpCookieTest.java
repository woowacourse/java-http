package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("특정 키로 쿠키 가져오기 성공")
    @Test
    void getCookies() {
        final HttpCookie httpCookie = new HttpCookie("TEST=test; TEST2=test");

        assertThat(httpCookie.getCookies("TEST")).isEqualTo("test");
        assertThat(httpCookie.getCookies("TEST2")).isEqualTo("test");
    }
}