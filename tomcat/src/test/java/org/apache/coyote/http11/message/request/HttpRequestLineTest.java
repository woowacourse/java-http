package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestLineTest {

    private HttpRequestLine httpRequestLine;

    @BeforeEach
    void setUp() {
        httpRequestLine = new HttpRequestLine(HttpMethod.GET, URI.create("/path"), "HTTP/1.1");
    }

    @DisplayName("HttpRequestLine 객체는 올바르게 초기화된다.")
    @Test
    void testConstructor() {
        // given
        HttpRequestLine requestLine = new HttpRequestLine("GET /path HTTP/1.1");

        // when
        HttpMethod method = requestLine.getMethod();
        URI uri = requestLine.getUri();
        String httpVersion = requestLine.getHttpVersion();

        // then
        assertAll(
                () -> assertThat(method).isEqualTo(HttpMethod.GET),
                () -> assertThat(uri.toString()).hasToString("/path"),
                () -> assertThat(httpVersion).isEqualTo("HTTP/1.1")
        );
    }

    @DisplayName("getMethod() 메서드는 요청 메서드를 반환한다.")
    @Test
    void getMethod() {
        // when
        HttpMethod method = httpRequestLine.getMethod();

        // then
        assertThat(method).isEqualTo(HttpMethod.GET);
    }

    @DisplayName("getUri() 메서드는 요청 URI를 반환한다.")
    @Test
    void getUri() {
        // when
        URI uri = httpRequestLine.getUri();

        // then
        assertThat(uri.toString()).hasToString("/path");
    }

    @DisplayName("hasPath() 메서드는 요청 URI와 경로가 일치하는지 확인한다.")
    @Test
    void hasPath() {
        // when
        boolean hasPath = httpRequestLine.hasPath("/path");

        // then
        assertThat(hasPath).isTrue();
    }

    @DisplayName("hasPath() 메서드는 요청 URI와 다른 경로가 일치하지 않는지 확인한다.")
    @Test
    void hasPathWithDifferentPath() {
        // when
        boolean hasPath = httpRequestLine.hasPath("/different-path");

        // then
        assertThat(hasPath).isFalse();
    }
}
