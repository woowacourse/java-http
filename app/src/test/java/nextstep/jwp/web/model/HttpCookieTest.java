package nextstep.jwp.web.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HttpCookieTest {

    @DisplayName("emptyCookies(): 값이 비어있는(0개) HttpCookie를 반환합니다.")
    @Test
    void emptyCookies() {
        // when
        HttpCookie httpCookie = HttpCookie.emptyCookies();

        // then
        assertThat(httpCookie.getValues()).isEmpty();
    }

    @DisplayName("JSESSIONID라는 세션ID의 쿠키가 존재하는지 확인한다.")
    @Test
    void hasJSessionId() {
        // given
        Map<String, Cookie> values = new HashMap<>();
        values.put("JSESSIONID", new Cookie("JSESSIONID", "value"));
        HttpCookie httpCookieWithSessionCookie = new HttpCookie(values);
        HttpCookie httpCookieWithoutSessionCookie = new HttpCookie(new HashMap<>());

        // when
        boolean result = httpCookieWithSessionCookie.hasJSessionId();
        boolean result2 = httpCookieWithoutSessionCookie.hasJSessionId();

        // then
        assertThat(result).isTrue();
        assertThat(result2).isFalse();
    }
}