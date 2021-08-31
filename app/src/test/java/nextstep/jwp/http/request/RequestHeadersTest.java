package nextstep.jwp.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("RequestHeaders 테스트")
class RequestHeadersTest {

    @DisplayName("쿠키와 쿠키 아닌 헤더 분리 테스트 - 쿠키가 없을 때")
    @Test
    void classifyHeadersWhenCookieNotExists() {
        //given
        final Map<String, String> headers = new ConcurrentHashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");

        //when
        //then
        assertThatCode(() -> new RequestHeaders(headers))
                .doesNotThrowAnyException();
    }

    @DisplayName("쿠키와 쿠키 아닌 헤더 분리 테스트 - 쿠키가 1개 존재할 때")
    @Test
    void classifyHeadersWhenOneCookieExists() {
        //given
        final Map<String, String> headers = new ConcurrentHashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", "yummy_cookie=choco");

        //when
        final RequestHeaders requestHeaders = new RequestHeaders(headers);
        final RequestCookie cookie = requestHeaders.getHttpCookie();

        //then
        assertThat(cookie.containsKey("yummy_cookie")).isTrue();
        assertThat(cookie.get("yummy_cookie")).isEqualTo("choco");
    }

    @DisplayName("쿠키와 쿠키 아닌 헤더 분리 테스트 - 쿠키가 2개 존재할 때")
    @Test
    void classifyHeadersWhenTwoCookiesExist() {
        //given
        final Map<String, String> headers = new ConcurrentHashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", "yummy_cookie=choco; tasty_cookie=strawberry");

        //when
        final RequestHeaders requestHeaders = new RequestHeaders(headers);
        final RequestCookie cookie = requestHeaders.getHttpCookie();

        //then
        assertThat(cookie.containsKey("yummy_cookie")).isTrue();
        assertThat(cookie.get("yummy_cookie")).isEqualTo("choco");

        assertThat(cookie.containsKey("tasty_cookie")).isTrue();
        assertThat(cookie.get("tasty_cookie")).isEqualTo("strawberry");
    }
}