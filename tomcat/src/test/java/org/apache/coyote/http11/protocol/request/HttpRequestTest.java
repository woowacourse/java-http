package org.apache.coyote.http11.protocol.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest 을 생성한다.")
    void createHttpRequest() throws IOException {
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of("Host: localhost:8080", "Connection: keep-alive"));
        RequestBody requestBody = new RequestBody("test body");
        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);

        assertAll(
                () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getPath()).isEqualTo("/index.html"),
                () -> assertThat(httpRequest.getHttpVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpRequest.getHeader("Host")).isEqualTo("localhost:8080"),
                () -> assertThat(httpRequest.getHeader("Connection")).isEqualTo("keep-alive"),
                () -> assertThat(httpRequest.getRequestBody().getText()).isEqualTo("test body")
        );
    }

    @Test
    @DisplayName("GET 메서드일 경우 RequestLine 에서 QueryParameter 를 반환한다.")
    void getQueryParameter() throws IOException {
        RequestLine requestLine = new RequestLine("GET /index.html?name=techcourse&age=27 HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of("Host: localhost:8080", "Connection: keep-alive"));
        RequestBody requestBody = new RequestBody("test body");
        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);

        assertAll(
                () -> assertThat(httpRequest.getParameter("name")).isEqualTo("techcourse"),
                () -> assertThat(httpRequest.getParameter("age")).isEqualTo("27")
        );
    }

    @Test
    @DisplayName("GET 메서드가 아닌 경우 RequestBody 에서 Parameter 를 반환한다.")
    void getParameter() throws IOException {
        RequestLine requestLine = new RequestLine("POST /index.html HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of("Host: localhost:8080", "Connection: keep-alive"));
        RequestBody requestBody = new RequestBody("name=techcourse&age=27");
        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);

        assertAll(
                () -> assertThat(httpRequest.getParameter("name")).isEqualTo("techcourse"),
                () -> assertThat(httpRequest.getParameter("age")).isEqualTo("27")
        );
    }


}
