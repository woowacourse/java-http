package org.apache.coyote.http11.parser;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.data.HttpCookie;
import org.apache.coyote.http11.data.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieParserTest {

    @Test
    @DisplayName("HTTP 요청의 쿠키를 파싱한다.")
    void parseCookiesFromRequest() {
        // given
        String rawRequestCookies = "JSESSIONID=session-id; EXTRA=hi";

        // when
        List<HttpCookie> cookies = HttpCookieParser.parseCookiesFromRequest(rawRequestCookies);

        // then
        assertThat(cookies).hasSize(2)
                .extracting(HttpCookie::getName)
                .containsAll(List.of("JSESSIONID", "EXTRA"));
    }

    @Test
    @DisplayName("HTTP 응답을 위한 쿠키 문자열을 생성한다.")
    void formatCookieForResponse() {
        // given
        HttpCookie cookie = new HttpCookie(HttpRequest.SESSION_ID_COOKIE_KEY, "session-id", Map.of("Max-Age", "600"));

        // when
        String rawCookie = HttpCookieParser.formatCookieForResponse(cookie);

        // then
        assertThat(rawCookie).isEqualTo("JSESSIONID=session-id; Max-Age=600");
    }
}
