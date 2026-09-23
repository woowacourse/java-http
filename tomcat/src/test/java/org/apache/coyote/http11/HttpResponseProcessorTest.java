package org.apache.coyote.http11;

import org.apache.coyote.http11.response.HttpResponseProcessor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HTTP 응답 작성")
class HttpResponseProcessorTest {

    @Test
    @DisplayName("쿠키를 주지 않으면 Set-Cookie를 내려보내지 않는다")
    void redirectWithoutCookie() throws IOException {
        // given
        final var outputStream = new ByteArrayOutputStream();
        final var responseProcessor = new HttpResponseProcessor(outputStream);

        // when
        responseProcessor.sendRedirect("/index.html");

        // then
        assertThat(output(outputStream)).isEqualTo(
                "HTTP/1.1 302 Found \r\n" +
                        "Location: /index.html \r\n" +
                        "Content-Length: 0 \r\n" +
                        "\r\n");
    }

    @Test
    @DisplayName("쿠키를 주면 Set-Cookie 헤더로 내려보낸다")
    void redirectWithCookie() throws IOException {
        // given
        final var outputStream = new ByteArrayOutputStream();
        final var responseProcessor = new HttpResponseProcessor(outputStream);

        // when
        responseProcessor.sendRedirect("/index.html", Cookies.of(new Cookie("JSESSIONID", "abc123")));

        // then
        assertThat(output(outputStream)).isEqualTo(
                "HTTP/1.1 302 Found \r\n" +
                        "Location: /index.html \r\n" +
                        "Content-Length: 0 \r\n" +
                        "Set-Cookie: JSESSIONID=abc123 \r\n" +
                        "\r\n");
    }

    @Test
    @DisplayName("쿠키가 여러 개면 Set-Cookie 헤더를 각각 내려보낸다")
    void redirectWithMultipleCookies() throws IOException {
        // given
        final var outputStream = new ByteArrayOutputStream();
        final var responseProcessor = new HttpResponseProcessor(outputStream);

        // when
        responseProcessor.sendRedirect("/index.html",
                Cookies.of(new Cookie("JSESSIONID", "abc123"), new Cookie("theme", "dark")));

        // then
        assertThat(output(outputStream)).isEqualTo(
                "HTTP/1.1 302 Found \r\n" +
                        "Location: /index.html \r\n" +
                        "Content-Length: 0 \r\n" +
                        "Set-Cookie: JSESSIONID=abc123 \r\n" +
                        "Set-Cookie: theme=dark \r\n" +
                        "\r\n");
    }

    private String output(ByteArrayOutputStream outputStream) {
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
