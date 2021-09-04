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

    @Test
    void hasJSessionId() {
        // given
        Map<String, Cookie> values = new HashMap<>();
        values.put("JSESSIONID", new Cookie("JSESSIONID", "value"));
        HttpCookie httpCookie = new HttpCookie(values);
        HttpCookie httpCookie2 = new HttpCookie(new HashMap<>());

        // when
        boolean result = httpCookie.hasJSessionId();
        boolean result2 = httpCookie2.hasJSessionId();

        // then
        assertThat(result).isTrue();
        assertThat(result2).isFalse();
    }
}