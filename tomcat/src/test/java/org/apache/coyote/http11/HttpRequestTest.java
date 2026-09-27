package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 요청_라인을_파싱한다() {
        final RequestLine requestLine = RequestLine.from("GET /login?redirect=index HTTP/1.1 ");

        assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getPath()).isEqualTo("/login");
        assertThat(requestLine.getProtocol()).isEqualTo("HTTP/1.1");
    }

    @Test
    void 잘못된_요청_라인이면_예외가_발생한다() {
        assertThatThrownBy(() -> RequestLine.from("GET /login"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 지원하지_않는_메서드면_예외가_발생한다() {
        assertThatThrownBy(() -> HttpMethod.from("FETCH"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 헤더와_쿠키를_파싱한다() throws IOException {
        final HttpRequest request = request(String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: yummy_cookie=choco; JSESSIONID=abc ",
                "",
                ""));

        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getPath()).isEqualTo("/index.html");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getCookie().getJSessionId()).isEqualTo("abc");
    }

    @Test
    void 본문의_폼_파라미터를_디코딩해서_파싱한다() throws IOException {
        final String body = "account=rudy&email=rudy%2Btest%40woowa.com";
        final HttpRequest request = request(String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                body));

        assertThat(request.getBodyParam("account")).isEqualTo("rudy");
        assertThat(request.getBodyParam("email")).isEqualTo("rudy+test@woowa.com");
    }

    @Test
    void Content_Length는_바이트_기준으로_본문을_읽는다() throws IOException {
        final String body = "account=" + "루디";
        final HttpRequest request = request(String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                body));

        assertThat(request.getBodyParam("account")).isEqualTo("루디");
    }

    @Test
    void 클라이언트가_데이터_없이_연결을_종료하면_null을_반환한다() throws IOException {
        assertThat(request("")).isNull();
    }

    @Test
    void 요청_라인이_빈_줄이면_예외가_발생한다() {
        assertThatThrownBy(() -> request("\r\n"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private HttpRequest request(final String raw) throws IOException {
        return HttpRequest.from(new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8)));
    }
}
