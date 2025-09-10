package org.apache.catalina.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.spring.http.HttpHeader;
import java.util.Arrays;
import java.util.List;
import com.spring.http.cookie.HttpCookies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

    @DisplayName("from 메서드: 같은 키 값의 헤더는 ,로 이어서 반환한다")
    @Test
    void parseTest1() {
        // given
        String requestLines = """
                GET /index.html HTTP/1.1
                Host: example.com
                Accept: text/html
                Accept: application/json
                """;

        final List<String> strings = Arrays.stream(requestLines.split("\n")).map(String::trim).toList();

        // when
        HttpHeader header = HttpHeader.from(strings);

        // then
        assertThat(header.get("Host")).isEqualTo("example.com");
        assertThat(header.get("Accept")).isEqualTo("text/html, application/json");
    }

    @DisplayName("getCookies 메서드: 쿠키가 있는 경우 HttpCookies 객체를 반환한다")
    @Test
    void getCookies_withCookies() {
        // given
        HttpHeader header = new HttpHeader();
        header.put("Cookie", "sessionId=abc123; userId=456; theme=dark");

        // when
        HttpCookies cookies = header.getCookies();

        // then
        assertThat(cookies.getCookie("sessionId")).hasToString("sessionId=abc123");
        assertThat(cookies.getCookie("userId")).hasToString("userId=456");
        assertThat(cookies.getCookie("theme")).hasToString("theme=dark");
    }

    @DisplayName("getCookies 메서드: 단일 쿠키가 있는 경우")
    @Test
    void getCookies_withSingleCookie() {
        // given
        HttpHeader header = new HttpHeader();
        header.put("Cookie", "sessionId=xyz789");

        // when
        HttpCookies cookies = header.getCookies();

        // then
        assertThat(cookies.getCookie("sessionId")).hasToString("sessionId=xyz789");
    }

    @DisplayName("hasCookie 메서드: 쿠키가 있는 경우 true를 반환한다")
    @Test
    void hasCookie_withCookies() {
        // given
        HttpHeader header = new HttpHeader();
        header.put("Cookie", "sessionId=abc123");

        // when & then
        assertThat(header.hasCookie()).isTrue();
    }

    @DisplayName("hasCookie 메서드: 쿠키가 없는 경우 false를 반환한다")
    @Test
    void hasCookie_noCookies() {
        // given
        HttpHeader header = new HttpHeader();

        // when & then
        assertThat(header.hasCookie()).isFalse();
    }

    @DisplayName("getCookies 메서드: 쿠키가 없는 경우 빈 HttpCookies를 반환한다")
    @Test
    void getCookies_noCookies() {
        // given
        HttpHeader header = new HttpHeader();

        // when
        HttpCookies cookies = header.getCookies();

        // then
        assertThat(cookies.toString()).isEmpty();
        assertThat(cookies.toString()).isEmpty();
    }

    @DisplayName("getCookies와 toString: 파싱된 쿠키를 toString으로 다시 변환할 수 있다")
    @Test
    void getCookies_toStringRoundTrip() {
        // given
        HttpHeader header = new HttpHeader();
        String originalCookieHeader = "sessionId=abc123; userId=456; theme=dark";
        header.put("Cookie", originalCookieHeader);

        // when
        HttpCookies cookies = header.getCookies();
        String reconstructed = cookies.toString();

        // then
        assertThat(reconstructed).isEqualTo("sessionId=abc123; userId=456; theme=dark");
    }


    @DisplayName("HttpHeader toString: 헤더 정보가 적절히 문자열로 변환된다")
    @Test
    void httpHeader_toString() {
        // given
        HttpHeader header = new HttpHeader();
        header.put("Host", "example.com");
        header.put("Content-Type", "application/json");
        header.put("Cookie", "sessionId=abc123; userId=456");

        // when
        String result = header.toString();

        // then
        assertThat(result).contains("Host")
                .contains("example.com")
                .contains("Content-Type")
                .contains("application/json")
                .contains("Cookie")
                .contains("sessionId=abc123; userId=456");

    }
}
