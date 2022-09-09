package nextstep.org.apache.coyote.http11.model;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.apache.coyote.http11.model.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HttpCookieTest {

    @Test
    @DisplayName("쿠키에 key값이 존재하면 value를 반환한다.")
    void getAttribute() {
        // given
        HttpCookie cookie = HttpCookie.from(Map.of("JSESSIONID", "123456"));

        // when, then
        assertThat(cookie.getAttributeOrDefault("JSESSIONID", "default")).isEqualTo("123456");
    }

    @Test
    @DisplayName("쿠키에 key값이 없으면 기본값을 반환한다.")
    void getDefaultValue() {
        // given
        HttpCookie cookie = HttpCookie.from(Map.of());

        // when, then
        assertThat(cookie.getAttributeOrDefault("JSESSIONID", "default")).isEqualTo("default");
    }
}
