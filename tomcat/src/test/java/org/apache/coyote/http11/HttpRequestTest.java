package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;

import org.apache.coyote.exception.InvalidRequestLineFormException;
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
                () -> assertThat(httpRequest.getMethod().getValue()).isEqualTo("GET"),
                () -> assertThat(httpRequest.getUri()).isEqualTo("/index.html"),
                () -> assertThat(httpRequest.getProtocolVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpRequestHeader.getHeaderValue("Host")).isEqualTo("localhost:8080 "),
                () -> assertThat(httpRequestHeader.getHeaderValue("Connection")).isEqualTo("keep-alive ")
        );
    }

    @Test
    void validateRequestLineValues() {
        String requestLine = "GET /index.html";
        List<String> requestHeader = List.of("Host: localhost:8080 ", "Connection: keep-alive ");

        assertThatThrownBy(() -> new HttpRequest(requestLine, requestHeader))
                .isInstanceOf(InvalidRequestLineFormException.class);
    }
}
