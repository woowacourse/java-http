package org.apache.coyote.http.request;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.apache.coyote.http.common.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest 객체 조립")
    void assembleHttpRequest() {
        // given
        final HttpRequestLine requestLine = HttpRequestLine.from("GET /path HTTP/1.1");
        final HttpRequestHeader requestHeader = HttpRequestHeader.from("Host: localhost:8080");
        final HttpRequestBody requestBody = HttpRequestBody.empty();
        // when
        final HttpRequest request = HttpRequest.from(requestLine, requestHeader, requestBody);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request).isNotNull();
            softly.assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(request.getPath()).isEqualTo("/path");
            softly.assertThat(request.getVersion()).isEqualTo("1.1");
            softly.assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
        });
    }

    @Test
    @DisplayName("POST 요청 HttpRequest 객체 조립")
    void assemblePostHttpRequest() {
        // given
        final HttpRequestLine requestLine = HttpRequestLine.from("POST /api/login HTTP/1.1");
        final HttpRequestHeader requestHeader = HttpRequestHeader.from(String.join("\r\n",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 27"));
        final HttpRequestBody requestBody = HttpRequestBody.from("account=user&password=1234", ContentType.FORM_URLENCODED);

        // when
        final HttpRequest request = HttpRequest.from(requestLine, requestHeader, requestBody);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
            softly.assertThat(request.getPath()).isEqualTo("/api/login");
            softly.assertThat(request.getVersion()).isEqualTo("1.1");
            softly.assertThat(request.getHeader("Host")).isEqualTo("localhost:8080");
            softly.assertThat(request.getContentType()).isEqualTo(ContentType.FORM_URLENCODED);
            softly.assertThat(request.getBodyParam("account")).isEqualTo("user");
            softly.assertThat(request.getBodyParam("password")).isEqualTo("1234");
        });
    }

    @Test
    @DisplayName("쿠키가 포함된 HttpRequest 객체 조립")
    void assembleHttpRequestWithCookies() {
        // given
        final HttpRequestLine requestLine = HttpRequestLine.from("GET /index.html HTTP/1.1");
        final HttpRequestHeader requestHeader = HttpRequestHeader.from(String.join("\r\n",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=ABC123; theme=dark"));
        final HttpRequestBody requestBody = HttpRequestBody.empty();

        // when
        final HttpRequest request = HttpRequest.from(requestLine, requestHeader, requestBody);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(request.getPath()).isEqualTo("/index.html");
            softly.assertThat(request.getCookie("JSESSIONID")).isEqualTo("ABC123");
            softly.assertThat(request.getCookie("theme")).isEqualTo("dark");
        });
    }

    @Test
    @DisplayName("쿼리 파라미터가 포함된 HttpRequest 객체 조립")
    void assembleHttpRequestWithQueryParams() {
        // given
        final HttpRequestLine requestLine = HttpRequestLine.from("GET /search?q=hello%20world HTTP/1.1");
        final HttpRequestHeader requestHeader = HttpRequestHeader.from("Host: localhost:8080");
        final HttpRequestBody requestBody = HttpRequestBody.empty();

        // when
        final HttpRequest request = HttpRequest.from(requestLine, requestHeader, requestBody);

        // then
        assertSoftly(softly -> {
            softly.assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(request.getPath()).isEqualTo("/search");
            softly.assertThat(request.getQueryParam("q")).isEqualTo("hello world");
        });
    }

    @Test
    @DisplayName("HttpRequest toString")
    void httpRequestToString() {
        // given
        final HttpRequestLine requestLine = HttpRequestLine.from("GET /path HTTP/1.1");
        final HttpRequestHeader requestHeader = HttpRequestHeader.from("Host: localhost:8080");
        final HttpRequestBody requestBody = HttpRequestBody.empty();

        // when
        final HttpRequest request = HttpRequest.from(requestLine, requestHeader, requestBody);
        final String result = request.toString();

        // then
        assertSoftly(softly -> {
            softly.assertThat(result).contains("GET /path HTTP/1.1");
            softly.assertThat(result).contains("Host: localhost:8080");
        });
    }
}
