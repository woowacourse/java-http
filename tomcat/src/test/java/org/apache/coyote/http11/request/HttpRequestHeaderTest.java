package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpRequestHeaderTest {

    @Test
    @DisplayName("정적 팩토리 메소드는 입력 받은 값을 파싱하여 headers에 저장한다.")
    void from() {
        // given
        final List<String> rawHeader = new ArrayList<>();
        rawHeader.add("name: eve");

        // when
        final HttpRequestHeader httpRequestHeader = HttpRequestHeader.from(rawHeader);

        // then
        assertThat(httpRequestHeader.getHeader("name")).isEqualTo("eve");
    }

    @Nested
    @DisplayName("getCookies 메소드는")
    class GetCookies {

        @Test
        @DisplayName("헤더에 쿠키 필드가 있다면 쿠키 필드의 값을 파싱한 HttpCookie 객체를 반환한다.")
        void success_withCookieField() {
            // given
            final List<String> rawHeader = new ArrayList<>();
            rawHeader.add("Cookie: name=cookie; flavor=choco");
            final HttpRequestHeader httpRequestHeader = HttpRequestHeader.from(rawHeader);

            // when
            final HttpCookie cookies = httpRequestHeader.getCookies();

            // then
            assertAll(() -> {
                assertThat(cookies.getCookie("name")).isEqualTo("cookie");
                assertThat(cookies.getCookie("flavor")).isEqualTo("choco");
            });
        }

        @Test
        @DisplayName("헤더에 쿠키 필드가 없다면 비어있는 HttpCookie 객체를 반환한다.")
        void success_noCookieFiled() {
            // given
            final List<String> rawHeader = new ArrayList<>();
            rawHeader.add("name: eve");
            final HttpRequestHeader httpRequestHeader = HttpRequestHeader.from(rawHeader);

            // when
            final HttpCookie cookies = httpRequestHeader.getCookies();

            // then
            final List<String> cookieNames = cookies.getCookieNames();
            assertThat(cookieNames).isEmpty();
        }
    }
}