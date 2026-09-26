package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("쿠키 모음")
class CookiesTest {

    @Test
    @DisplayName("Cookie 헤더의 쿠키를 모두 읽는다")
    void parseCookieHeader() {
        // when
        final Cookies cookies = Cookies.from("JSESSIONID=abc123; theme=dark");

        // then
        assertThat(cookies.find("JSESSIONID")).isPresent();
        assertThat(cookies.find("JSESSIONID").get().value()).isEqualTo("abc123");
        assertThat(cookies.find("theme").get().value()).isEqualTo("dark");
    }

    @Test
    @DisplayName("쿠키 사이의 공백을 이름에 포함하지 않는다")
    void stripWhitespaceAroundPair() {
        // when
        final Cookies cookies = Cookies.from("a=1;   b=2");

        // then
        assertThat(cookies.find("b")).isPresent();
    }

    @Test
    @DisplayName("값에 등호가 있어도 잘리지 않는다")
    void keepEqualSignInsideValue() {
        // when
        final Cookies cookies = Cookies.from("JSESSIONID=YWJjMTIz==");

        // then
        assertThat(cookies.find("JSESSIONID").get().value()).isEqualTo("YWJjMTIz==");
    }

    @Test
    @DisplayName("값을 디코딩하지 않고 그대로 둔다")
    void keepValueUndecoded() {
        // when
        final Cookies cookies = Cookies.from("email=a%40b.com");

        // then
        assertThat(cookies.find("email").get().value()).isEqualTo("a%40b.com");
    }

    @Test
    @DisplayName("Cookie 헤더가 없으면 비어 있다")
    void emptyWhenHeaderIsMissing() {
        // expect
        assertThat(Cookies.from(null).isEmpty()).isTrue();
        assertThat(Cookies.from("   ").isEmpty()).isTrue();
    }

    @Test
    @DisplayName("형식이 어긋난 구간은 건너뛴다")
    void skipMalformedPair() {
        // when
        final Cookies cookies = Cookies.from("JSESSIONID=abc123; broken; theme=dark");

        // then
        assertThat(cookies.find("JSESSIONID")).isPresent();
        assertThat(cookies.find("theme")).isPresent();
        assertThat(cookies.find("broken")).isEmpty();
    }

    @Test
    @DisplayName("찾는 쿠키가 없으면 빈 값을 반환한다")
    void emptyWhenCookieNotFound() {
        // expect
        assertThat(Cookies.from("theme=dark").find("JSESSIONID")).isEmpty();
    }

    @Test
    @DisplayName("쿠키를 직접 담아 만들 수 있다")
    void createFromCookies() {
        // when
        final Cookies cookies = Cookies.of(new Cookie("JSESSIONID", "abc123"));

        // then
        assertThat(cookies.find("JSESSIONID").get().toHeaderValue()).isEqualTo("JSESSIONID=abc123");
        assertThat(cookies.values()).hasSize(1);
    }
}
