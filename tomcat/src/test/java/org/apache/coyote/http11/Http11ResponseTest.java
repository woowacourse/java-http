package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11ResponseTest {

    @DisplayName("Http11Response를 만든다.")
    @Test
    void of() throws IOException {
        String requestString = "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 9\n" +
                "Accept: text/html\n" +
                "\n";

        Http11Request request = Http11Request.from(
                new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8)));

        Http11Response response = Http11Response.of(request);

        assertThat(response.getStatusLine().getStatusLine()).isEqualTo("HTTP/1.1 200 OK ");
    }

    @DisplayName("리다이렉션 한다.")
    @Test
    void setRedirectionResponse() throws IOException {
        String requestString = "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 9\n" +
                "Accept: text/html\n" +
                "\n";

        Http11Request request = Http11Request.from(
                new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8)));

        Http11Response http11Response = Http11Response.of(request);

        http11Response.setRedirectResponse("/index.html", 302, List.of());

        assertThat(http11Response.getStatusLine().getStatusLine()).isEqualTo("HTTP/1.1 302 FOUND ");
        assertThat(http11Response.getResponseHeader().getHeader("Location")).isEqualTo("/index.html");
    }

    @DisplayName("정적 페이지를 응답한다.")
    @Test
    void setStaticResponse() throws IOException {
        String requestString = "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 9\n" +
                "Accept: text/html\n" +
                "\n";

        Http11Request request = Http11Request.from(
                new ByteArrayInputStream(requestString.getBytes(StandardCharsets.UTF_8)));

        Http11Response http11Response = Http11Response.of(request);

        http11Response.setStaticResponse(request, "/401.html", 401);

        assertThat(http11Response.getStatusLine().getStatusLine()).isEqualTo("HTTP/1.1 401 UNAUTHORIZED ");
    }
}
