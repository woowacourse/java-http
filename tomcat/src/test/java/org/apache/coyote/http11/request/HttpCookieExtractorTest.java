package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class HttpCookieExtractorTest {

    private static final String cookies = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

    @Test
    @DisplayName("헤더에서 쿠키를 추출한다.")
    void should_separate_cookie_from_request_header() {
        //given
        HttpRequestHeaders httpRequestHeaders = new HttpRequestHeaders(Map.of("Cookie", cookies));

        //when
        HttpCookie httpCookie = HttpCookieExtractor.extractCookie(httpRequestHeaders);

        //then
        assertThat(httpCookie.getCookieValue("yummy_cookie")).isEqualTo("choco");
        assertThat(httpCookie.getCookieValue("tasty_cookie")).isEqualTo("strawberry");
        assertThat(httpCookie.getCookieValue("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}
