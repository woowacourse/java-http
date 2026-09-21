package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    @Test
    void parsesRequestLineHeadersAndBody() throws IOException {
        BufferedReader reader = readerOf(String.join("\r\n",
                "POST /login?next=home HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 14",
                "",
                "account=junior"
        ));

        HttpRequest request = HttpRequest.parse(reader);

        assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(request.getRequestUri().getPath()).isEqualTo("/login");
        assertThat(request.getRequestUri().getQueryParameter("next")).isEqualTo("home");
        assertThat(request.getHeader("content-type"))
                .isEqualTo("application/x-www-form-urlencoded");
        assertThat(request.getBody()).isEqualTo("account=junior");
    }

    @Test
    void rejectsBodyShorterThanContentLength() {
        BufferedReader reader = readerOf(String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: 4",
                "",
                "abc"
        ));

        assertThatThrownBy(() -> HttpRequest.parse(reader))
                .isInstanceOf(IOException.class)
                .hasMessage("요청 본문이 Content-Length보다 짧습니다.");
    }

    @Test
    void parsesUrlEncodedBodyParameters() throws IOException {
        String body = "email=user%40example.com&nickname=hello+world";
        BufferedReader reader = readerOf(String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.length(),
                "",
                body
        ));

        HttpRequest request = HttpRequest.parse(reader);

        assertThat(request.getParameter("email")).isEqualTo("user@example.com");
        assertThat(request.getParameter("nickname")).isEqualTo("hello world");
    }

    private BufferedReader readerOf(String request) {
        return new BufferedReader(new StringReader(request));
    }
}
