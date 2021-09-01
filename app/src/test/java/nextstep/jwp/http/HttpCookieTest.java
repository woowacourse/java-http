package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    private final HttpHeader header = new HttpHeader(new HashMap<>(Map.of(
        "Cookie", "cookie=delicious; JSESSIONID=sth"
    )));

    @DisplayName("헤더로부터 쿠키를 생성한다.")
    @Test
    void createFromCookie() {
        HttpCookie httpCookie = HttpCookie.fromHeader(header);

        assertThat(httpCookie.getAllCookies().keySet()).containsExactly("cookie", "JSESSIONID");
        assertThat(httpCookie.getAllCookies().values()).containsExactly("delicious", "sth");
    }

    @DisplayName("쿠키를 헤더 타입에 맞게 변환한다.")
    @Test
    void toHeaderValue() {
        HttpCookie httpCookie = HttpCookie.fromHeader(header);
        Map<String, String> cookieHeader = httpCookie.toHeaderFormat();

        assertThat(cookieHeader.get("Set-Cookie")).isEqualTo("cookie=delicious; JSESSIONID=sth");
    }
}