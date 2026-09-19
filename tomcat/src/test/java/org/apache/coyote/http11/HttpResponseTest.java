package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("응답 쿠키를 Set-Cookie 헤더로 변환한다")
    void convertsCookieToSetCookieHeader() {
        // given
        final HttpResponse response = new HttpResponse(HttpStatus.OK, "text/html;charset=utf-8", "Hello world!")
                .withCookie(Cookie.of("JSESSIONID", "abc-123"));

        // when
        final String actual = new String(response.toBytes());

        // then
        assertThat(actual).contains("Set-Cookie: JSESSIONID=abc-123");
    }

    @Test
    void OK_응답을_바이트로_변환한다() {
        HttpResponse response = new HttpResponse(HttpStatus.OK,"text/html;charset=utf-8", "Hello world!");

        String actual = new String(response.toBytes());

        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(actual).isEqualTo(expected);
    }
}
