package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.header.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    @DisplayName("리다이렉트 응답에 302 상태와 Location 헤더를 추가한다")
    void createsRedirectResponse() {
        // given
        final HttpResponse response = HttpResponse.redirect("/index.html");

        // when
        final String actual = new String(response.toBytes());

        // then
        assertThat(actual)
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
    }

    @Test
    @DisplayName("응답 쿠키를 Set-Cookie 헤더로 변환한다")
    void convertsCookieToSetCookieHeader() {
        // given
        final HttpResponse response = new HttpResponse(HttpStatus.OK, "text/html;charset=utf-8", "Hello world!")
                .addHeader("Set-Cookie", Cookie.of("JSESSIONID", "abc-123").toHeaderValue());

        // when
        final String actual = new String(response.toBytes());

        // then
        assertThat(actual).contains("Set-Cookie: JSESSIONID=abc-123");
    }

    @Test
    @DisplayName("OK 응답을 바이트로 변환한다")
    void convertsOkResponseToBytes() {
        final HttpResponse response = HttpResponse.ok("text/html;charset=utf-8", "Hello world!");

        final String actual = new String(response.toBytes());

        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("같은 이름의 응답 헤더를 여러 개 추가한다")
    void addsMultipleHeadersWithSameName() {
        // given
        final HttpResponse response = new HttpResponse(HttpStatus.OK, "text/html;charset=utf-8", "")
                .addHeader("Set-Cookie", "JSESSIONID=abc-123")
                .addHeader("Set-Cookie", "theme=dark");

        // when
        final String actual = new String(response.toBytes());

        // then
        assertThat(actual)
                .contains("Set-Cookie: JSESSIONID=abc-123")
                .contains("Set-Cookie: theme=dark");
    }
}
