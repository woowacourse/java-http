package org.apache.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.http.HttpCookie;
import org.apache.http.HttpMethod;
import org.apache.http.header.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest 객체 생성 및 기본 getter 메소드 테스트")
    void createHttpRequest() {
        // given
        String method = "GET";
        String path = "/index.html";
        String version = "HTTP/1.1";
        HttpHeader[] headers = new HttpHeader[]{
                new HttpHeader("Host", "localhost:8080"),
                new HttpHeader("Connection", "keep-alive")
        };
        String body = "sample body";

        // when
        HttpRequest request = new HttpRequest(method, path, version, headers, body);

        // then
        assertAll(
                () -> assertThat(request.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(request.getPath()).isEqualTo(path),
                () -> assertThat(request.getVersion()).isEqualTo(version),
                () -> assertThat(request.getHeaders()).hasSize(2),
                () -> assertThat(request.getBody()).isEqualTo(body)

        );
    }

    @Test
    @DisplayName("isSameMethod 메소드 테스트")
    void iSameMethod() {
        // given
        HttpRequest request = new HttpRequest("POST", "/login", "HTTP/1.1", null, null);

        // then
        assertAll(
                () -> assertThat(request.isSameMethod(HttpMethod.POST)).isTrue(),
                () -> assertThat(request.isSameMethod(HttpMethod.GET)).isFalse()
        );
    }

    @Test
    @DisplayName("getHeader 메소드 테스트: 포함되지 않은 헤더인 경우 예외")
    void testGetHeader() {
        // given
        HttpHeader[] headers = new HttpHeader[]{
                new HttpHeader("Content-Type", "application/json"),
                new HttpHeader("Authorization", "Bearer token")
        };
        HttpRequest request = new HttpRequest("GET", "/api", "HTTP/1.1", headers, null);

        // when & then
        assertAll(
                () -> assertThat(request.getHeader("Content-Type")).isEqualTo("application/json"),
                () -> assertThat(request.getHeader("Authorization")).isEqualTo("Bearer token"),
                () -> assertThatThrownBy(() -> request.getHeader("Non-Existent"))
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("존재 하지 않는 Header")
        );
    }

    @Test
    @DisplayName("HttpCookie 파싱 테스트")
    void testParseCookie() {
        // given
        HttpHeader[] headers = new HttpHeader[]{
                new HttpHeader("Cookie", "sessionId=abc123; userId=john")
        };
        HttpRequest request = new HttpRequest("GET", "/", "HTTP/1.1", headers, null);

        // when
        HttpCookie cookie = request.getHttpCookie();

        // then
        assertAll(
                () -> assertThat(cookie).isNotNull(),
                () -> assertThat(cookie.getValue("sessionId")).isEqualTo("abc123"),
                () -> assertThat(cookie.getValue("userId")).isEqualTo("john")
        );
    }

    @Test
    @DisplayName("Cookie 헤더가 없는 경우 HttpCookie null 테스트")
    void testNoCookie() {
        // given
        HttpHeader[] headers = new HttpHeader[]{
                new HttpHeader("Content-Type", "text/html")
        };
        HttpRequest request = new HttpRequest("GET", "/", "HTTP/1.1", headers, null);

        assertThat(request.getHttpCookie()).isNull();
    }
}

