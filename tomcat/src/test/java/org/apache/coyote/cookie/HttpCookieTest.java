package org.apache.coyote.cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Nested
    class parse_cookie {

        @Test
        void 쿠키에_등호가_없는_경우_해당_쿠키만_무시한다() {
            String invalidCookie = "JSESSIONID";

            HttpCookie httpCookie = HttpCookie.from(invalidCookie);

            assertThat(httpCookie.cookies()).isEmpty();
        }

        @Test
        void 쿠키_이름이_없는_경우_해당_쿠키만_무시한다() {
            String invalidCookie = "=session-id";

            HttpCookie httpCookie = HttpCookie.from(invalidCookie);

            assertThat(httpCookie.cookies()).isEmpty();
        }

        @Test
        void 잘못된_쿠키가_포함되어도_정상적인_쿠키는_파싱한다() {
            HttpCookie httpCookie = HttpCookie.from("JSESSIONID; token=abc; =invalid");

            assertThat(httpCookie.getValue("token")).hasValue("abc");
        }

        @Test
        void 쿠키_값에_등호가_포함되어도_정상_생성된다() {
            HttpCookie httpCookie = HttpCookie.from("token=a=b");

            assertThat(httpCookie.getValue("token")).hasValue("a=b");
        }

        @Test
        void 쿠키_값이_비어있어도_정상_생성된다() {
            HttpCookie httpCookie = HttpCookie.from("JSESSIONID=");

            assertThat(httpCookie.getValue("JSESSIONID")).hasValue("");
        }
    }

    @Test
    void Cookie_헤더의_이름이_포함된_경우_예외가_발생한다() {
        String containingCookieHeaderName = "Cookie: yummy_cookie=choco";
        assertThatThrownBy(() -> HttpCookie.from(containingCookieHeaderName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 존재하지_않는_쿠키를_조회하면_빈_값을_반환한다() {
        String cookieHeader = "yummy_cookie=choco";

        HttpCookie httpCookie = HttpCookie.from(cookieHeader);

        assertThat(httpCookie.getValue("tasty_cookie")).isEmpty();
    }

    @Test
    void 정상_생성() {
        String validCookieHeader = "yummy_cookie=choco; tasty_cookie=strawberry";

        HttpCookie httpCookie = HttpCookie.from(validCookieHeader);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(httpCookie.getValue("yummy_cookie")).hasValue("choco");
            softly.assertThat(httpCookie.getValue("tasty_cookie")).hasValue("strawberry");
        });
    }
}
