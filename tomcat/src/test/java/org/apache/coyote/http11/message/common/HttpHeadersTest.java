package org.apache.coyote.http11.message.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.HttpCookies;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    private HttpHeaders httpHeaders;

    @BeforeEach
    void setUp() {
        httpHeaders = new HttpHeaders();
    }

    @DisplayName("String 형태 header를 통해 HttpHeaders를 생성한다.")
    @Test
    void parseHeaders() {
        String type = "text/html;charset=utf-8";
        String contentType = "Content-Type: " + type;

        int length = 123;
        String contentLength = "Content-Length: " + length;

        String cookie = "sessionId=abc123";
        String cookies = "Cookie: " + cookie;

        String headers = contentType + "\n" +
                contentLength + "\n" +
                cookies;

        HttpHeaders parsedHeaders = new HttpHeaders(headers);

        assertAll(
                () -> assertThat(parsedHeaders.getContentType().getType()).isEqualTo(type),
                () -> assertThat(parsedHeaders.getContentLength()).isEqualTo(length),
                () -> assertThat(parsedHeaders.getCookies().toString()).isEqualTo(cookie)
        );
    }

    @DisplayName("헤더를 설정하면, 해당 헤더가 포함된다.")
    @Test
    void testSetHeaders() {
        // given
        HttpHeaders httpHeaders = new HttpHeaders();

        // when
        httpHeaders.setHeaders("Custom-Header", "CustomValue");

        // then
        assertThat(httpHeaders.toString()).contains("Custom-Header: CustomValue");
    }

    @DisplayName("Content-Length가 설정되지 않았을 때 기본값은 0이다.")
    @Test
    void contentLengthWithDefault() {
        // given
        HttpHeaders httpHeaders = new HttpHeaders();

        // when
        int contentLength = httpHeaders.getContentLength();

        // then
        assertThat(contentLength).isEqualTo(0);
    }

    @DisplayName("Content-Type이 설정되지 않았을 때 기본값은 text/html이다.")
    @Test
    void getContentTypeWithDefault() {
        // given
        HttpHeaders httpHeaders = new HttpHeaders();

        // when
        ContentType contentType = httpHeaders.getContentType();

        // then
        assertThat(contentType).isEqualTo(ContentType.TEXT_HTML);
    }

    @DisplayName("Cookie 헤더를 설정하면, 해당 쿠키가 반환된다.")
    @Test
    void getCookies() {
        // given
        HttpHeaders httpHeaders = new HttpHeaders();
        String expectedCookie = "sessionId=xyz789";
        httpHeaders.setHeaders("Cookie", expectedCookie);

        // when
        HttpCookies cookies = httpHeaders.getCookies();

        // then
        assertThat(cookies.toString()).isEqualTo(expectedCookie);
    }

    @DisplayName("toString 메서드는 설정된 헤더들을 올바른 형식으로 반환한다.")
    @Test
    void toStringTest() {
        // given
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setHeaders("Content-Type", "application/json");
        httpHeaders.setHeaders("Content-Length", "456");

        // when
        String result = httpHeaders.toString();

        // then
        assertAll(
                () -> assertThat(result).contains("Content-Type: application/json"),
                () -> assertThat(result).contains("Content-Length: 456")
        );
    }
}
