package org.apache.coyote.http11.response;

import org.apache.coyote.HttpStatus;
import org.apache.coyote.MimeType;
import org.apache.coyote.http11.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("HTTP 응답 메시지 구성")
class HttpResponseTest {

    @Test
    @DisplayName("쿠키를 추가하지 않으면 Set-Cookie를 내려보내지 않는다")
    void redirectWithoutCookie() {
        // given
        final var response = new HttpResponse();

        // when
        response.setRedirect("/index.html");

        // then
        assertThat(message(response)).isEqualTo(
                "HTTP/1.1 302 Found \r\n" +
                        "Location: /index.html \r\n" +
                        "Content-Length: 0 \r\n" +
                        "\r\n");
    }

    @Test
    @DisplayName("쿠키를 추가하면 Set-Cookie 헤더로 내려보낸다")
    void redirectWithCookie() {
        // given
        final var response = new HttpResponse();

        // when
        response.setRedirect("/index.html");
        response.addCookie(new Cookie("JSESSIONID", "abc123"));

        // then
        assertThat(message(response)).isEqualTo(
                "HTTP/1.1 302 Found \r\n" +
                        "Location: /index.html \r\n" +
                        "Content-Length: 0 \r\n" +
                        "Set-Cookie: JSESSIONID=abc123 \r\n" +
                        "\r\n");
    }

    @Test
    @DisplayName("쿠키가 여러 개면 추가한 순서대로 Set-Cookie 헤더를 각각 내려보낸다")
    void redirectWithMultipleCookies() {
        // given
        final var response = new HttpResponse();

        // when
        response.setRedirect("/index.html");
        response.addCookie(new Cookie("JSESSIONID", "abc123"));
        response.addCookie(new Cookie("theme", "dark"));

        // then
        assertThat(message(response)).isEqualTo(
                "HTTP/1.1 302 Found \r\n" +
                        "Location: /index.html \r\n" +
                        "Content-Length: 0 \r\n" +
                        "Set-Cookie: JSESSIONID=abc123 \r\n" +
                        "Set-Cookie: theme=dark \r\n" +
                        "\r\n");
    }

    @Test
    @DisplayName("본문을 설정하면 Content-Type과 Content-Length를 함께 내려보낸다")
    void body() {
        // given
        final var response = new HttpResponse();

        // when
        response.setBody(MimeType.TEXT_HTML, "Hello world!".getBytes(StandardCharsets.UTF_8));

        // then
        assertThat(message(response)).isEqualTo(
                "HTTP/1.1 200 OK \r\n" +
                        "Content-Type: text/html;charset=utf-8 \r\n" +
                        "Content-Length: 12 \r\n" +
                        "\r\n" +
                        "Hello world!");
    }

    @Test
    @DisplayName("에러 상태를 설정하면 빈 본문의 에러 응답을 내려보낸다")
    void error() {
        // given
        final var response = new HttpResponse();

        // when
        response.setError(HttpStatus.NOT_FOUND);

        // then
        assertThat(message(response)).isEqualTo(
                "HTTP/1.1 404 Not Found \r\n" +
                        "Content-Type: text/html;charset=utf-8 \r\n" +
                        "Content-Length: 0 \r\n" +
                        "\r\n");
    }

    @Test
    @DisplayName("에러가 아닌 상태로 에러 응답을 만들 수 없다")
    void errorWithNonErrorStatus() {
        // given
        final var response = new HttpResponse();

        // when & then
        assertThatThrownBy(() -> response.setError(HttpStatus.OK))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private String message(HttpResponse response) {
        return new String(response.toHttpBytes(), StandardCharsets.UTF_8);
    }
}
