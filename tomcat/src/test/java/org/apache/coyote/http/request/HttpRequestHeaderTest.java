package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import org.apache.coyote.http.HttpCookie;
import org.junit.jupiter.api.Test;

public class HttpRequestHeaderTest {

    @Test
    void 헤더를_파싱할_수_있다() {
        // given
        List<String> lines = List.of(
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 123",
                "Cookie: sessionId=abc123; username=John"
        );

        // when
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(lines);

        // then
        assertThat(httpRequestHeader.get("Host")).isEqualTo("localhost:8080");
        assertThat(httpRequestHeader.get("Connection")).isEqualTo("keep-alive");
        assertThat(httpRequestHeader.get("Content-Length")).isEqualTo("123");
        assertThat(httpRequestHeader.get("Cookie")).isEqualTo("sessionId=abc123; username=John");
    }

    @Test
    void 쿠키를_파싱할_수_있다() {
        // given
        List<String> lines = List.of(
                "Cookie: JSESSIONID=abc123; username=John"
        );

        // when
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(lines);

        // then
        Optional<HttpCookie> sessionCookie = httpRequestHeader.findCookie("JSESSIONID");
        Optional<HttpCookie> usernameCookie = httpRequestHeader.findCookie("username");

        assertThat(sessionCookie).isPresent();
        assertThat(sessionCookie.get().getValue()).isEqualTo("abc123");

        assertThat(usernameCookie).isPresent();
        assertThat(usernameCookie.get().getValue()).isEqualTo("John");
    }

    @Test
    void 쿠키가_헤더에_존재하지_않을_때_널값을_반환한다() {
        // given
        List<String> lines = List.of(
                "Host: localhost:8080"
        );

        // when
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(lines);

        // then
        assertThat(httpRequestHeader.findCookie("cookieName")).isEmpty();
    }

    @Test
    void ContentLength를_가져올_수_있다() {
        // given
        List<String> lines = List.of(
                "Content-Length: 123"
        );

        // when
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(lines);

        // then
        assertThat(httpRequestHeader.getContentLength()).isEqualTo(123);
    }

    @Test
    void ContentLength가_헤더에_존재하지_않을_때_기본값을_가져올_수_있다() {
        // given
        List<String> lines = List.of();

        // when
        HttpRequestHeader httpRequestHeader = new HttpRequestHeader(lines);

        // then
        assertThat(httpRequestHeader.getContentLength()).isEqualTo(0);
    }
}
