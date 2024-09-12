package org.apache.catalina.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CookiesTest {

    @Nested
    class 쿠키 {

        @Test
        void 쿠키를_추가한다() {
            // given
            Cookies cookies = new Cookies();
            String sorce = "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

            // when
            cookies.add(sorce);

            // then
            assertThat(cookies.findCookie("JSESSIONID").get()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
        }

        @Test
        void 쿠키를_여러개_추가한다() {
            // given
            Cookies cookies = new Cookies();
            String sorce = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

            // when
            cookies.add(sorce);

            // then
            assertAll(
                    () -> assertThat(cookies.findCookie("yummy_cookie").get()).isEqualTo("choco"),
                    () -> assertThat(cookies.findCookie("tasty_cookie").get()).isEqualTo("strawberry"),
                    () -> assertThat(cookies.findCookie("JSESSIONID").get()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46")
            );
        }
    }
}
