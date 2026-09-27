package org.apache.coyote.http11.request;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void 요청_라인의_메서드와_경로를_제공한다() {
        final HttpRequest request = request("POST /login HTTP/1.1", List.of(), "");

        assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(request.getPath()).isEqualTo("/login");
    }

    @Test
    void 쿼리_값에_인코딩된_구분자가_있어도_값의_일부로_읽는다() {
        final HttpRequest request = request(
                "GET /login?account=a%26b&password=p%3D1 HTTP/1.1", List.of(), "");

        assertThat(request.getParameter("account")).isEqualTo("a&b");
        assertThat(request.getParameter("password")).isEqualTo("p=1");
        assertThat(request.getParameter("b")).isNull();
    }

    @Test
    void 폼_본문의_파라미터를_제공한다() {
        final HttpRequest request = request(
                "POST /login HTTP/1.1",
                List.of("Content-Type: application/x-www-form-urlencoded"),
                "account=gugu&password=password");

        assertThat(request.getParameter("account")).isEqualTo("gugu");
        assertThat(request.getParameter("password")).isEqualTo("password");
    }

    @Test
    void 쿠키_헤더를_HttpCookie로_제공한다() {
        final HttpRequest request = request(
                "GET /index.html HTTP/1.1",
                List.of("Cookie: yummy_cookie=choco; JSESSIONID=656cef62"),
                "");

        assertThat(request.getCookie().get("JSESSIONID")).isEqualTo("656cef62");
    }

    @Test
    void 쿠키_헤더가_없어도_빈_쿠키를_제공한다() {
        final HttpRequest request = request("GET /index.html HTTP/1.1", List.of(), "");

        assertThat(request.getCookie().get("JSESSIONID")).isNull();
    }

    private HttpRequest request(final String requestLine, final List<String> headerLines, final String body) {
        return HttpRequest.of(RequestLine.from(requestLine), RequestHeaders.from(headerLines), body);
    }
}
