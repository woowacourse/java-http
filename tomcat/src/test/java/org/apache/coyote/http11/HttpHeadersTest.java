package org.apache.coyote.http11;

import static org.apache.coyote.http11.HttpHeaderName.CONTENT_LENGTH;
import static org.apache.coyote.http11.HttpHeaderName.CONTENT_TYPE;
import static org.apache.coyote.http11.HttpHeaderName.COOKIE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @DisplayName("헤더를 생성한다.")
    @Test
    void from() throws IOException {
        // given
        try (final BufferedReader bufferedReader = new BufferedReader(new StringReader(
            String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=password"
            )))
        ) {
            // when
            final HttpHeaders headers = HttpHeaders.from(bufferedReader);

            // then
            assertAll(
                () -> headers.containsHeaderNameAndValue(HttpHeaderName.HOST, "localhost:8080"),
                () -> headers.containsHeaderNameAndValue(HttpHeaderName.CONNECTION, "keep-alive"),
                () -> headers.containsHeaderNameAndValue(CONTENT_LENGTH, "30"),
                () -> headers.containsHeaderNameAndValue(CONTENT_TYPE,
                    "application/x-www-form-urlencoded")
            );
        }
    }

    @DisplayName("헤더에 쿠키가 존재하면, 쿠키 헤더를 추가한다.")
    @Test
    void parseCookie() throws IOException {
        // given
        try (final BufferedReader bufferedReader = new BufferedReader(new StringReader(
            String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Cookie: JSESSIONID=asdf1234",
                "",
                "account=gugu&password=password"
            )))
        ) {
            // when
            final HttpHeaders headers = HttpHeaders.from(bufferedReader);

            // then
            assertThat(headers.containsHeaderNameAndValue(COOKIE, "JSESSIONID=asdf1234")).isTrue();
        }
    }

    @DisplayName("응답 헤더를 생성한다.")
    @Test
    void makeHttpResponseHeaders() {
        // given

        // when
        final HttpHeaders httpHeaders = HttpHeaders.makeHttpResponseHeaders("text/html", "body");

        // then
        assertAll(
            () -> assertThat(httpHeaders.containsHeaderNameAndValue(CONTENT_TYPE, "text/html")).isTrue(),
            () -> assertThat(httpHeaders.containsHeaderNameAndValue(CONTENT_LENGTH, "4")).isTrue()
        );
    }

    @DisplayName("Content-Length 값을 조회한다.")
    @Test
    void getContentLength() throws IOException {
        // given
        try (final BufferedReader bufferedReader = new BufferedReader(new StringReader(
            String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Cookie: JSESSIONID=asdf1234",
                "",
                "account=gugu&password=password"
            )))
        ) {
            // when
            final HttpHeaders headers = HttpHeaders.from(bufferedReader);
            final HttpHeaders emptyHeaders = new HttpHeaders();

            // then
            assertAll(
                () -> assertThat(headers.getContentLength()).isEqualTo(30),
                () -> assertThat(emptyHeaders.getContentLength()).isEqualTo(0)
            );
        }
    }

    @DisplayName("Cookie 헤더가 존재하면 쿠키를 리턴한다.")
    @Test
    void getCookie() throws IOException {
        // given
        try (final BufferedReader bufferedReader = new BufferedReader(new StringReader(
            String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Cookie: JSESSIONID=asdf1234",
                "",
                "account=gugu&password=password"
            )))
        ) {
            // when
            final HttpHeaders headers = HttpHeaders.from(bufferedReader);
            final HttpHeaders emptyHeaders = new HttpHeaders();
            final HttpCookie httpCookie = headers.getCookie();

            // then
            assertAll(
                () -> assertThat(httpCookie.getJSessionID()).isEqualTo("asdf1234"),
                () -> assertThatThrownBy(emptyHeaders::getCookie)
                    .isInstanceOf(IllegalStateException.class)
            );
        }
    }

    @DisplayName("헤더를 문자열로 변경한다.")
    @Test
    void headerToString() throws IOException {
        // given
        try (final BufferedReader bufferedReader = new BufferedReader(new StringReader(
            String.join("\r\n",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=password"
            )))
        ) {
            // when
            final HttpHeaders headers = HttpHeaders.from(bufferedReader);
            final String headersString = headers.toString();

            // then
            assertThat(headersString).isEqualTo(String.join("\r\n",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Host: localhost:8080",
                ""
            ));
        }
    }
}
