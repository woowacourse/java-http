package nextstep.jwp.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpCookieTest {

    @Test
    void generateCookie() {
        // given
        Map<String, String> header = Map.of("Cookie", "value=yummyCookie; JSESSIONID=1234");

        // when
        HttpCookie cookie = HttpCookie.from(header);

        // then
        assertThat(cookie.getJsessionId()).isEqualTo("1234");
    }
}
