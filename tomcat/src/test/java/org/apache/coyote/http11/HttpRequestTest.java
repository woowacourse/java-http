package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void 요청_라인의_메서드와_경로를_제공한다() throws IOException {
        final HttpRequest request = parse(
                "POST /login HTTP/1.1",
                "",
                "");

        assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(request.getPath()).isEqualTo("/login");
    }

    @Test
    void 쿼리_값에_인코딩된_구분자가_있어도_값의_일부로_읽는다() throws IOException {
        final HttpRequest request = parse(
                "GET /login?account=a%26b&password=p%3D1 HTTP/1.1",
                "",
                "");

        assertThat(request.getParameter("account")).isEqualTo("a&b");
        assertThat(request.getParameter("password")).isEqualTo("p=1");
        assertThat(request.getParameter("b")).isNull();
    }

    @Test
    void 본문을_Content_Length_만큼_읽는다() throws IOException {
        final HttpRequest request = parse(
                "POST /login HTTP/1.1",
                "Content-Length: 4",
                "",
                "body");

        assertThat(request.getBody()).isEqualTo("body");
    }

    @Test
    void 폼_바디의_파라미터를_읽는다() throws IOException {
        final HttpRequest request = parse(
                "POST /login HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");

        assertThat(request.getParameter("account")).isEqualTo("gugu");
        assertThat(request.getParameter("password")).isEqualTo("password");
    }

    @Test
    void 쿠키_헤더를_HttpCookie로_제공한다() throws IOException {
        final HttpRequest request = parse(
                "GET /index.html HTTP/1.1",
                "Cookie: yummy_cookie=choco; JSESSIONID=656cef62",
                "",
                "");

        assertThat(request.getCookie().get("JSESSIONID")).isEqualTo("656cef62");
    }

    @Test
    void 쿠키_헤더가_없어도_빈_쿠키를_제공한다() throws IOException {
        final HttpRequest request = parse(
                "GET /index.html HTTP/1.1",
                "",
                "");

        assertThat(request.getCookie().get("JSESSIONID")).isNull();
    }

    @Test
    void 요청이_없으면_빈_값을_반환한다() throws IOException {
        final BufferedReader reader = new BufferedReader(new StringReader(""));

        assertThat(HttpRequest.from(reader)).isEmpty();
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
