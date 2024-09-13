package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHeadersTest {

    @DisplayName("쿠키 이름을 입력하면 해당 쿠키의 value를 응답한다.")
    @Test
    void getCookieValue() {
        RequestHeaders requestHeaders = new RequestHeaders(Map.of("Cookie", "JSESSIONID=1234"));
        String jsessionId = requestHeaders.getCookieValue("JSESSIONID");

        assertThat(jsessionId).isEqualTo("1234");
    }

    @DisplayName("쿠키 헤더가 존재하지 않는 경우, 예외가 발생한다.")
    @Test
    void getCookieValueNoCookieHeader() {
        RequestHeaders requestHeaders = new RequestHeaders(Map.of());

        assertThatThrownBy(() -> requestHeaders.getCookieValue("JSESSIONID"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cookie header not exists.");
    }

    @DisplayName("Cookie 헤더는 존재하지만, Cookie 헤더에 입력한 값이 존재하지 않는 경우, 예외가 발생한다.")
    @Test
    void getCookieValueNoSuchCookie() {
        RequestHeaders requestHeaders = new RequestHeaders(Map.of("Cookie", "JSESSIONID=1234"));

        assertThatThrownBy(() -> requestHeaders.getCookieValue("RandomCookieName"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("No such Cookie.");
    }
}
