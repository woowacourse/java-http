package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 요청_라인의_메서드_URI_경로를_조회한다() throws IOException {
        HttpRequest request = new HttpRequest(new BufferedReader(new StringReader(
                "GET /login?account=gugu HTTP/1.1\r\n\r\n")));

        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getUri()).isEqualTo("/login?account=gugu");
        assertThat(request.getPath()).isEqualTo("/login");
    }

    @Test
    void 헤더를_읽고_값에_포함된_콜론을_보존한다() throws IOException {
        HttpRequest request = new HttpRequest(new BufferedReader(new StringReader(
                "POST /login HTTP/1.1\r\n"
                        + "Host: localhost:8080\r\n"
                        + "Content-Length: 12\r\n"
                        + "Cookie: JSESSIONID=abc\r\n\r\naccount=gugu")));

        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getContentLength()).isEqualTo(12);
        assertThat(request.getHeader("Cookie")).isEqualTo("JSESSIONID=abc");
    }

    @Test
    void 헤더_이름은_대소문자를_구분하지_않는다() throws IOException {
        HttpRequest request = new HttpRequest(new BufferedReader(new StringReader(
                "GET /login HTTP/1.1\r\ncookie: JSESSIONID=abc\r\n\r\n")));

        assertThat(request.getHeader("Cookie")).isEqualTo("JSESSIONID=abc");
    }

    @Test
    void 헤더가_없으면_빈_문자열과_본문_길이_0을_반환한다() throws IOException {
        HttpRequest request = new HttpRequest(new BufferedReader(new StringReader(
                "GET /login HTTP/1.1\r\n\r\n")));

        assertThat(request.getHeader("Cookie")).isEmpty();
        assertThat(request.getContentLength()).isZero();
    }

    @Test
    void ContentLength만큼_본문을_읽고_파라미터를_조회한다() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(
                "POST /login HTTP/1.1\r\nContent-Length: 12\r\n\r\naccount=guguNEXT"));

        HttpRequest request = new HttpRequest(reader);

        assertThat(request.getParameter("account")).isEqualTo("gugu");
        assertThat(reader.readLine()).isEqualTo("NEXT");
    }

    @Test
    void 본문이_없으면_파라미터도_없다() throws IOException {
        HttpRequest request = post("");

        assertThat(request.getParameters()).isEmpty();
    }

    @Test
    void 빈_파라미터_값과_없는_파라미터를_구분한다() throws IOException {
        HttpRequest request = post("account=gugu&password=");

        assertThat(request.getParameter("password")).isEmpty();
        assertThat(request.getParameter("email")).isNull();
    }

    @Test
    void 파라미터를_나눈_뒤_URL_디코딩한다() throws IOException {
        HttpRequest request = post("account=gugu&password=a%26b%3Dc");

        assertThat(request.getParameter("password")).isEqualTo("a&b=c");
    }

    private HttpRequest post(String body) throws IOException {
        return new HttpRequest(new BufferedReader(new StringReader(
                "POST /login HTTP/1.1\r\nContent-Length: " + body.length() + "\r\n\r\n" + body)));
    }
}
