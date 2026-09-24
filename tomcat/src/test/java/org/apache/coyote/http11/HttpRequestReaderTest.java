package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestReaderTest {

    @Test
    void 요청_라인과_헤더와_본문을_읽는다() throws IOException {
        final HttpRequest request = read(
                "POST /login HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Content-Length: 4\r\n" +
                "\r\n" +
                "body").orElseThrow();

        assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getBody()).isEqualTo("body");
    }

    @Test
    void 본문을_Content_Length_바이트만큼_읽는다() throws IOException {
        final String body = "name=홍길동";
        final int byteLength = body.getBytes(UTF_8).length;

        final HttpRequest request = read(
                "POST /register HTTP/1.1\r\n" +
                "Content-Length: " + byteLength + "\r\n" +
                "\r\n" +
                body).orElseThrow();

        assertThat(request.getBody()).isEqualTo(body);
    }

    @Test
    void 본문이_Content_Length보다_짧으면_예외가_발생한다() {
        final HttpRequestReader reader = readerOf(
                "POST /login HTTP/1.1\r\n" +
                "Content-Length: 30\r\n" +
                "\r\n" +
                "account=gugu");

        assertThatThrownBy(reader::read)
                .isInstanceOf(HttpRequestParseException.class);
    }

    @Test
    void 줄_끝이_LF만_있어도_읽는다() throws IOException {
        final HttpRequest request = read(
                "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "\n").orElseThrow();

        assertThat(request.getPath()).isEqualTo("/index.html");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
    }

    @Test
    void 요청이_없으면_빈_값을_반환한다() throws IOException {
        assertThat(read("")).isEmpty();
    }

    @Test
    void 헤더_도중_입력이_끝나도_읽은_헤더까지_읽는다() throws IOException {
        final HttpRequest request = read(
                "GET / HTTP/1.1\r\n" +
                "Host: localhost:8080").orElseThrow();

        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
    }

    private Optional<HttpRequest> read(final String message) throws IOException {
        return readerOf(message).read();
    }

    private HttpRequestReader readerOf(final String message) {
        return new HttpRequestReader(new ByteArrayInputStream(message.getBytes(UTF_8)));
    }
}
