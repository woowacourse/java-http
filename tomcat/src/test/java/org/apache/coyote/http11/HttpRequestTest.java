package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void POST_요청의_헤더와_폼_파라미터를_파싱한다() throws IOException {
        String body = "account=gugu&password=password";
        String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.length(),
                "",
                body);

        HttpRequest request = HttpRequest.from(new BufferedReader(new StringReader(rawRequest)));

        assertThat(request.isPost()).isTrue();
        assertThat(request.getPath()).isEqualTo("/login.html");
        assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(request.getParameter("account")).isEqualTo("gugu");
        assertThat(request.getParameter("password")).isEqualTo("password");
    }

    @Test
    void 쿠키에_JSESSIONID가_없으면_새_세션을_만든다() throws IOException {
        String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Cookie: yummy_cookie=choco",
                "",
                "");

        HttpRequest request = HttpRequest.from(new BufferedReader(new StringReader(rawRequest)));

        assertThat(request.getSession()).isNotNull();
        assertThat(request.isNewSession()).isTrue();
        assertThat(request.getCookie().get("yummy_cookie")).isEqualTo("choco");
    }
}
