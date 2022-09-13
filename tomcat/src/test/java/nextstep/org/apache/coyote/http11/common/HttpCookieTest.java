package nextstep.org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.common.HttpCookie;
import org.junit.jupiter.api.Test;

public class HttpCookieTest {

    @Test
    void createCookie() {
        final String cookie = "JSESSIONID=1234";
        final HttpCookie httpCookie = HttpCookie.from(cookie);

        assertThat(httpCookie.getSession()).isEqualTo("1234");
    }
}
