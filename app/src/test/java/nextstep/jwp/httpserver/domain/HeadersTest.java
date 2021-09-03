package nextstep.jwp.httpserver.domain;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Headers 단위 테스트")
class HeadersTest {

    @Test
    @DisplayName("헤더 추가")
    void addHeader() {
        // given
        Headers headers = new Headers();
        int originalSize = headers.getHeaders().size();

        // when
        headers.addHeader("Location", "1");
        int actual = headers.getHeaders().size();

        // then
        assertThat(actual).isEqualTo(originalSize + 1);
    }

    @Test
    @DisplayName("content-length 헤더가 없는 경우 0을 리턴")
    void noContentLength() {
        // given
        Headers headers = new Headers();

        // when
        String contentLength = headers.contentLength();

        // then
        assertThat(contentLength).isEqualTo("0");
    }

    @Test
    @DisplayName("content-length 헤더 찾기")
    void contentLength() {
        // given
        Headers headers = new Headers();
        headers.addHeader("Content-Length", "10");

        // when
        String contentLength = headers.contentLength();

        // then
        assertThat(contentLength).isEqualTo("10");
    }

    @Test
    @DisplayName("http 응답 형태로 변환하기")
    void responseFormat() {
        // given
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Content-Length", "10");
        headerMap.put("Location", "1");
        Headers headers = new Headers(headerMap);

        // when
        String result = headers.responseFormat();

        // then
        assertThat(result).isEqualTo(
                String.join("\r\n",
                        "Content-Length: 10 ",
                        "Location: 1 ")
        );
    }

    @Test
    @DisplayName("header에 쿠키가 있는 경우")
    void cookieInHeader() {
        // given
        Map<String, String> headerMap = new HashMap<>();
        headerMap.put("Cookie", "name=air");
        headerMap.put("Location", "1");
        Headers headers = new Headers(headerMap);

        // when
        String cookie = headers.getCookie();

        // then
        assertThat(cookie).isEqualTo("name=air");
    }

    @Test
    @DisplayName("header에 쿠키가 없는 경우")
    void noCookieInHeader() {
        // given
        Headers headers = new Headers();

        // when
        String cookie = headers.getCookie();

        // then
        assertThat(cookie).isEqualTo("");
    }
}
