package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    @Test
    void 요청_라인을_파싱한다() throws IOException {
        final HttpRequest request = parse(
                "GET /index.html HTTP/1.1",
                "",
                "");

        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/index.html");
        assertThat(request.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void 쿼리_스트링을_경로와_분리해_파싱한다() throws IOException {
        final HttpRequest request = parse(
                "GET /login?account=gugu&password=password HTTP/1.1",
                "",
                "");

        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getQueryParameter("account")).isEqualTo("gugu");
        assertThat(request.getQueryParameter("password")).isEqualTo("password");
    }

    @Test
    void 값이_없는_쿼리_파라미터는_빈_문자열이다() throws IOException {
        final HttpRequest request = parse(
                "GET /login?account=&password HTTP/1.1",
                "",
                "");

        assertThat(request.getQueryParameter("account")).isEmpty();
        assertThat(request.getQueryParameter("password")).isEmpty();
    }

    @Test
    void 헤더를_파싱한다() throws IOException {
        final HttpRequest request = parse(
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "",
                "");

        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getHeader("Accept")).isEqualTo("text/html");
    }

    @Test
    void 헤더는_빈_줄까지만_읽는다() throws IOException {
        final BufferedReader reader = readerOf(
                "POST /login HTTP/1.1",
                "Content-Length: 4",
                "",
                "body");

        HttpRequest.from(reader);

        assertThat(reader.readLine()).isEqualTo("body");
    }

    @Test
    void 요청이_없으면_빈_값을_반환한다() throws IOException {
        final BufferedReader reader = new BufferedReader(new StringReader(""));

        assertThat(HttpRequest.from(reader)).isEmpty();
    }

    @Test
    void 요청_라인_형식이_잘못되면_예외가_발생한다() {
        final BufferedReader reader = readerOf("GET /index.html", "", "");

        assertThatThrownBy(() -> HttpRequest.from(reader))
                .isInstanceOf(HttpRequestParseException.class);
    }

    @Test
    void 헤더_형식이_잘못되면_예외가_발생한다() {
        final BufferedReader reader = readerOf("GET / HTTP/1.1", "InvalidHeader", "", "");

        assertThatThrownBy(() -> HttpRequest.from(reader))
                .isInstanceOf(HttpRequestParseException.class);
    }

    @Test
    void 헤더_도중_입력이_끝나도_읽은_헤더까지_파싱한다() throws IOException {
        final BufferedReader reader = readerOf(
                "GET / HTTP/1.1",
                "Host: localhost:8080");

        final HttpRequest request = HttpRequest.from(reader).orElseThrow();

        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
    }

    private HttpRequest parse(String... lines) throws IOException {
        return HttpRequest.from(readerOf(lines)).orElseThrow();
    }

    private BufferedReader readerOf(String... lines) {
        return new BufferedReader(new StringReader(String.join("\r\n", lines)));
    }
}
