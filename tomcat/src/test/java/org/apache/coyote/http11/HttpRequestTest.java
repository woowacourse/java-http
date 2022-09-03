package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("HttpRequest 객체를 올바르게 생성한다.")
    @Test
    void createHttpRequest() {
        String requestLine = "GET /index.html HTTP/1.1 ";
        List<String> requestHeader = List.of("Host: localhost:8080 ", "Connection: keep-alive ");

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeader);
        HttpRequestHeader httpRequestHeader = httpRequest.getHttpRequestHeader();
        assertAll(
                () -> assertThat(httpRequest.getMethod().equals("GET")),
                () -> assertThat(httpRequest.getUri().equals("/index.html")),
                () -> assertThat(httpRequest.getProtocolVersion().equals("HTTP/1.1")),
                () -> assertThat(httpRequestHeader.getHeaderValue("Host").equals("localhost:8080")),
                () -> assertThat(httpRequestHeader.getHeaderValue("Connection").equals("keep-alive"))
        );
    }
}
