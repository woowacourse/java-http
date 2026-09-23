package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CookiesTest {

    @Test
    void 여러_쿠키_중_JSESSIONID를_조회한다() {
        // given
        Cookies cookies = new Cookies("theme=dark; JSESSIONID=session-id; locale=ko");

        // when
        String sessionId = cookies.getValue("JSESSIONID");

        // then
        assertThat(sessionId).isEqualTo("session-id");
    }

    @Test
    void Cookie_헤더에_JSESSIONID가_없으면_null을_반환한다() {
        // given
        Cookies cookies = new Cookies("theme=dark; locale=ko");

        // when
        String sessionId = cookies.getValue("JSESSIONID");

        // then
        assertThat(sessionId).isNull();
    }

    @Test
    void 쿠키_값에_등호가_있어도_값을_보존한다() {
        // given
        Cookies cookies = new Cookies("token=abc=def");

        // when
        String token = cookies.getValue("token");

        // then
        assertThat(token).isEqualTo("abc=def");
    }
}
