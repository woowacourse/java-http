package nextstep.jwp.http.message.request;

import nextstep.jwp.http.message.element.Cookie;
import nextstep.jwp.http.message.element.Headers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HeadersTest {

    @Test
    void getHeader() {
        final Headers headers = new Headers();

        headers.putHeader("Host", "localhost:8080");
        headers.putHeader("Connection", "keep-alive");
        headers.putHeader("Accept", "*/*");

        final String actual = headers.getHeader("Host")
            .orElseThrow(IllegalArgumentException::new);

        assertThat(actual).isEqualTo("localhost:8080");
    }

    @DisplayName("쿠키가 존재하면 쿠키를 추출한다.")
    @Test
    void getCookie_exist() {
        Map<String, String> values = new HashMap<>();
        values.put("Cookie", "test1=1; test2=2");
        Headers headers = new Headers(values);

        Cookie cookie = headers.getCookie();

        assertThat(cookie.get("test1")).get().isEqualTo("1");
        assertThat(cookie.get("test2")).get().isEqualTo("2");
    }

    @DisplayName("쿠키가 존재하지 않으면 비어있다.")
    @Test
    void getCookie_noValues() {
        Headers headers = new Headers();

        Cookie cookie = headers.getCookie();

        assertThat(cookie.size()).isZero();
    }
}
