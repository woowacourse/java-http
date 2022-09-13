package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    @DisplayName("정적 팩토리 메소드는 입력 받은 Cookie 헤더 값을 파싱하여 cookies에 저장한다.")
    void from() {
        // given
        final String rawCookie ="name=cookie; flavor=peanut; amount=10";

        // when
        final HttpCookie cookies = HttpCookie.from(rawCookie);

        // then
        assertAll(() -> {
            assertThat(cookies.getCookie("name")).isEqualTo("cookie");
            assertThat(cookies.getCookie("flavor")).isEqualTo("peanut");
            assertThat(cookies.getCookie("amount")).isEqualTo("10");
        });
    }
}