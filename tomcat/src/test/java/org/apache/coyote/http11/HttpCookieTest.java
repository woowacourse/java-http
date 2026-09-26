package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class HttpCookieTest {

    private static final String COOKIE_HEADER =
            "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

    @Nested
    @DisplayName("쿠키 헤더를 파싱한다")
    class Parse {

        @Test
        @DisplayName("여러 개의 쿠키를 이름으로 꺼낼 수 있다")
        void parsesEveryCookie() {
            final HttpCookie cookie = new HttpCookie(COOKIE_HEADER);

            assertThat(cookie.get("yummy_cookie")).isEqualTo("choco");
            assertThat(cookie.get("tasty_cookie")).isEqualTo("strawberry");
            assertThat(cookie.get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
        }

        @Test
        @DisplayName("구분자 뒤의 공백을 이름에 포함하지 않는다")
        void trimsWhitespaceAroundName() {
            final HttpCookie cookie = new HttpCookie("a=1;   b=2;c=3");

            assertThat(cookie.get("b")).isEqualTo("2");
            assertThat(cookie.get("c")).isEqualTo("3");
            assertThat(cookie.get(" b")).isNull();
        }

        @Test
        @DisplayName("값에 '='가 들어 있어도 첫 '='만 기준으로 나눈다")
        void splitsOnFirstEqualSignOnly() {
            final HttpCookie cookie = new HttpCookie("token=abc=def==");

            assertThat(cookie.get("token")).isEqualTo("abc=def==");
        }

        @Test
        @DisplayName("'='가 없는 조각은 무시한다")
        void ignoresPairWithoutEqualSign() {
            final HttpCookie cookie = new HttpCookie("broken; JSESSIONID=abc");

            assertThat(cookie.get("broken")).isNull();
            assertThat(cookie.get("JSESSIONID")).isEqualTo("abc");
        }
    }

    @Nested
    @DisplayName("쿠키가 없는 요청도 정상으로 다룬다")
    class Absent {

        @Test
        @DisplayName("헤더가 null이어도 예외가 나지 않는다")
        void doesNotThrowWhenHeaderIsNull() {
            assertThatCode(() -> new HttpCookie(null)).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("헤더가 null이면 어떤 이름으로 조회해도 null이다")
        void returnsNullWhenHeaderIsNull() {
            final HttpCookie cookie = new HttpCookie(null);

            assertThat(cookie.get("JSESSIONID")).isNull();
        }

        @Test
        @DisplayName("헤더가 빈 문자열이어도 예외 없이 빈 쿠키가 된다")
        void handlesEmptyHeader() {
            final HttpCookie cookie = new HttpCookie("");

            assertThat(cookie.get("JSESSIONID")).isNull();
        }

        @Test
        @DisplayName("담겨 있지 않은 이름으로 조회하면 null이다")
        void returnsNullForUnknownName() {
            final HttpCookie cookie = new HttpCookie("other=value");

            assertThat(cookie.get("JSESSIONID")).isNull();
        }
    }
}
