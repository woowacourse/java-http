package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void POST_요청의_폼_파라미터를_해석한다() {
        HttpRequest request = HttpRequest.from(
                List.of("POST /login HTTP/1.1", "Content-Length: 30"),
                "account=gugu&password=password");

        assertThat(request.isPost()).isTrue();
        assertThat(request.getPath()).isEqualTo("/login");
        assertThat(request.getParameter("account")).isEqualTo("gugu");
        assertThat(request.getParameter("password")).isEqualTo("password");
    }

    @Test
    void Cookie_헤더에서_세션_ID를_찾는다() {
        HttpRequest request = HttpRequest.from(
                List.of("GET /index.html HTTP/1.1", "Cookie: yummy_cookie=choco; JSESSIONID=abc"),
                "");

        assertThat(request.getSessionId()).isEqualTo("abc");
    }
}
