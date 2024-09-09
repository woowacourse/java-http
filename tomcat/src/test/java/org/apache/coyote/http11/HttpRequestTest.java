package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("헤더에서 쿠키 필드를 추출한다.")
    @Test
    void should_extractCookies_whenHaveCookies() {
        // given
        HttpRequest request = new HttpRequest("1.1",
                "GET",
                "/path",
                Map.of("Cookie", "k1=v1; k2=v2; k3=v3"),
                "body");

        // when
        HttpCookie httpCookie = request.extractCookie();

        // then
        assertThat(httpCookie.getCookie("k1")).isEqualTo("v1");
        assertThat(httpCookie.getCookie("k2")).isEqualTo("v2");
        assertThat(httpCookie.getCookie("k3")).isEqualTo("v3");
    }
}
