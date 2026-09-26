package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void POST_요청의_헤더와_폼_파라미터를_파싱한다() throws IOException {
        String body = "account=gugu&password=password";
        HttpRequest request = parse(
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "",
                body);

        assertThat(request.isPost()).isTrue();
        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getParameter("account")).isEqualTo("gugu");
        assertThat(request.getParameter("password")).isEqualTo("password");
    }

    @Test
    void Cookie_헤더를_파싱한다() throws IOException {
        HttpRequest request = parse(
                "GET /index.html HTTP/1.1",
                "Cookie: yummy_cookie=choco; JSESSIONID=abc",
                "",
                "");

        assertThat(request.getCookie().get("yummy_cookie")).isEqualTo("choco");
        assertThat(request.getCookie().getJSessionId()).isEqualTo("abc");
    }

    private HttpRequest parse(String... lines) throws IOException {
        return HttpRequest.from(new BufferedReader(new StringReader(String.join("\r\n", lines))));
    }
}
