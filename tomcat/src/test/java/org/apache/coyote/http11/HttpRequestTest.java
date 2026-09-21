package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    @Test
    void parsesRequestLineHeadersAndBody() throws IOException {
        InputStream inputStream = inputStreamOf(String.join("\r\n",
                "POST /login?next=home HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 14",
                "",
                "account=junior"
        ));

        HttpRequest request = HttpRequest.parse(inputStream);

        assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(request.getRequestUri().getPath()).isEqualTo("/login");
        assertThat(request.getRequestUri().getQueryParameter("next")).isEqualTo("home");
        assertThat(request.getHeader("content-type"))
                .isEqualTo("application/x-www-form-urlencoded");
        assertThat(request.getHeader("Content-Type"))
                .isEqualTo("application/x-www-form-urlencoded");
        assertThat(request.getBody()).isEqualTo("account=junior");
    }

    @Test
    void rejectsBodyShorterThanContentLength() {
        InputStream inputStream = inputStreamOf(String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: 4",
                "",
                "abc"
        ));

        assertThatThrownBy(() -> HttpRequest.parse(inputStream))
                .isInstanceOf(HttpRequestParseException.class)
                .hasMessage("요청 본문이 Content-Length보다 짧습니다.");
    }

    @Test
    void parsesUrlEncodedBodyParameters() throws IOException {
        String body = "email=user%40example.com&nickname=hello+world";
        InputStream inputStream = inputStreamOf(String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.length(),
                "",
                body
        ));

        HttpRequest request = HttpRequest.parse(inputStream);

        assertThat(request.getParameter("email")).isEqualTo("user@example.com");
        assertThat(request.getParameter("nickname")).isEqualTo("hello world");
    }

    @Test
    void normalizesHeaderNamesPassedToConstructor() {
        HttpRequest request = new HttpRequest(
                RequestLine.parse("POST /login HTTP/1.1"),
                Map.of("Content-Type", "application/x-www-form-urlencoded"),
                "account=junior"
        );

        assertThat(request.getHeader("content-type"))
                .isEqualTo("application/x-www-form-urlencoded");
        assertThat(request.getHeader("CONTENT-TYPE"))
                .isEqualTo("application/x-www-form-urlencoded");
        assertThat(request.getParameter("account")).isEqualTo("junior");
    }

    @Test
    void readsBodyUsingContentLengthInBytes() throws IOException {
        String body = "안녕하세요";
        InputStream inputStream = inputStreamOf(String.join("\r\n",
                "POST /message HTTP/1.1",
                "Content-Type: text/plain;charset=utf-8",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body
        ));

        HttpRequest request = HttpRequest.parse(inputStream);

        assertThat(request.getBody()).isEqualTo(body);
    }

    private InputStream inputStreamOf(String request) {
        return new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
    }
}
