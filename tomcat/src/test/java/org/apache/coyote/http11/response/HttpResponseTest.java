package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpStatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("HttpStatusCode와 ResponseBody로 HttpResponse를 만들 수 있다.")
    @Test
    void of() {
        // given
        final ResponseBody responseBody = ResponseBody.of("index.html", "/index.html");
        final HttpStatusCode httpStatusCode = HttpStatusCode.OK;

        // when
        final HttpResponse httpResponse = HttpResponse.of(httpStatusCode, responseBody);

        // then
        assertAll(
                () -> assertThat(httpResponse.getStatusLine()).usingRecursiveComparison()
                        .isEqualTo(new StatusLine(httpStatusCode)),
                () -> assertThat(httpResponse.getResponseHeaders()).usingRecursiveComparison()
                        .isEqualTo(ResponseHeader.from(responseBody)),
                () -> assertThat(httpResponse.getResponseBody()).isEqualTo(responseBody)
        );
    }

    @DisplayName("원하는 Cookie 정보를 추가한 Response를 만들 수 있다.")
    @Test
    void withCookie() {
        // given
        final ResponseBody responseBody = ResponseBody.of("index.html", "/index.html");
        final HttpStatusCode httpStatusCode = HttpStatusCode.OK;
        final String cookieHeader = "cookie1=one;cookie2=two;cookie3=three";
        final HttpCookie cookie = HttpCookie.from(cookieHeader);

        // when
        final HttpResponse httpResponse = HttpResponse.withCookie(httpStatusCode, responseBody, cookie);

        // then
        assertThat(httpResponse.getResponseHeaders().getValue("Set-Cookie")).isEqualTo(cookieHeader);
    }
}
