package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @DisplayName("InputStream으로부터 여러 헤더를 파싱할 수 있다.")
    @Test
    void from_parsesMultipleHeaders() throws IOException {
        // given
        final String rawHeaders = "Host: localhost:8080\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Accept: */*\r\n" +
                "\r\n";
        final InputStream inputStream = toInputStream(rawHeaders);

        // when
        final HttpHeaders httpHeaders = HttpHeaders.from(inputStream);

        // then
        final var headers = httpHeaders.getHeaders();
        assertAll(
                () -> assertThat(headers).hasSize(3),
                () -> assertThat(headers.get("Host")).containsExactly("localhost:8080"),
                () -> assertThat(headers.get("Content-Type")).containsExactly("text/html;charset=utf-8"),
                () -> assertThat(headers.get("Accept")).containsExactly("*/*")
        );
    }

    @DisplayName("콜론 주변의 다양한 공백을 처리할 수 있다.")
    @Test
    void from_handlesVariousWhitespace() throws IOException {
        // given
        final String rawHeaders = "Key1:Value1\r\n" +
                "Key2: Value2\r\n" +
                "Key3 : Value3\r\n" +
                "\r\n";
        final InputStream inputStream = toInputStream(rawHeaders);

        // when
        final HttpHeaders httpHeaders = HttpHeaders.from(inputStream);

        // then
        final var headers = httpHeaders.getHeaders();
        assertAll(
                () -> assertThat(headers.get("Key1")).containsExactly("Value1"),
                () -> assertThat(headers.get("Key2")).containsExactly("Value2"),
                () -> assertThat(headers.get("Key3")).containsExactly("Value3")
        );
    }

    @DisplayName("동일한 키를 가진 여러 헤더 값을 리스트로 저장한다.")
    @Test
    void from_handlesMultiValueHeaders() throws IOException {
        // given
        final String rawHeaders = "Accept: text/html\r\n" +
                "Accept: application/xhtml+xml\r\n" +
                "Accept: application/xml;q=0.9\r\n" +
                "\r\n";
        final InputStream inputStream = toInputStream(rawHeaders);

        // when
        final HttpHeaders httpHeaders = HttpHeaders.from(inputStream);

        // then
        final var headers = httpHeaders.getHeaders();
        assertThat(headers.get("Accept"))
                .containsExactly("text/html", "application/xhtml+xml", "application/xml;q=0.9");
    }

    @DisplayName("Content-Length 헤더가 있으면 해당 값을 정수로 반환한다.")
    @Test
    void getContentLength_withHeader() throws IOException {
        // given
        final String rawHeaders = "Content-Length: 256\r\n\r\n";
        final InputStream inputStream = toInputStream(rawHeaders);
        final HttpHeaders httpHeaders = HttpHeaders.from(inputStream);

        // when
        final int contentLength = httpHeaders.getContentLength();

        // then
        assertThat(contentLength).isEqualTo(256);
    }

    @DisplayName("Content-Length 헤더가 없으면 0을 반환한다.")
    @Test
    void getContentLength_withoutHeader() throws IOException {
        // given
        final String rawHeaders = "Host: localhost:8080\r\n\r\n";
        final InputStream inputStream = toInputStream(rawHeaders);
        final HttpHeaders httpHeaders = HttpHeaders.from(inputStream);

        // when
        final int contentLength = httpHeaders.getContentLength();

        // then
        assertThat(contentLength).isZero();
    }

    @DisplayName("Cookie 헤더가 있을 경우 HttpCookies 객체를 반환한다.")
    @Test
    void getCookies_withCookieHeader() throws IOException {
        // given
        final String rawHeaders = "Cookie: JSESSIONID=1234abcd; lang=ko\r\n\r\n";
        final InputStream inputStream = toInputStream(rawHeaders);
        final HttpHeaders httpHeaders = HttpHeaders.from(inputStream);

        // when
        final HttpCookies cookies = httpHeaders.getCookies();

        // then
        assertAll(
                () -> assertThat(cookies.isEmpty()).isFalse(),
                () -> assertThat(cookies.getCookie("JSESSIONID")).isPresent(),
                () -> assertThat(cookies.getCookie("JSESSIONID").get().getValue()).isEqualTo("1234abcd")
        );
    }

    @DisplayName("Cookie 헤더가 없을 경우 비어있는 HttpCookies 객체를 반환한다.")
    @Test
    void getCookies_withoutCookieHeader() throws IOException {
        // given
        final String rawHeaders = "Host: localhost:8080\r\n\r\n";
        final InputStream inputStream = toInputStream(rawHeaders);
        final HttpHeaders httpHeaders = HttpHeaders.from(inputStream);

        // when
        final HttpCookies cookies = httpHeaders.getCookies();

        // then
        assertThat(cookies.isEmpty()).isTrue();
    }

    private InputStream toInputStream(final String text) {
        return new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
    }
}
