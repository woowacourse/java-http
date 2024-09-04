package org.apache.coyote.http11.domain.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.domain.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest 을 생성한다.")
    void createHttpRequest() throws IOException {
        String line = "GET /index.html HTTP/1.1";
        List<String> headerLines = List.of("Host: localhost:8080", "Connection: keep-alive");

        HttpRequest httpRequest = new HttpRequest(line, headerLines);

        assertAll(
                () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getPath()).isEqualTo("/index.html"),
                () -> assertThat(httpRequest.getHttpVersion()).isEqualTo("HTTP/1.1")
        );
    }

    @Test
    @DisplayName("HttpRequest 을 생성할 때 첫줄에 빈 라인이 들어오면 예외를 던진다.")
    void createHttpRequestWithEmptyLine() {
        String line = "";
        List<String> headerLines = List.of("Host: localhost:8080", "Connection: keep-alive");

        assertThatThrownBy(() -> new HttpRequest(line, headerLines))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Line is Empty");
    }
}
