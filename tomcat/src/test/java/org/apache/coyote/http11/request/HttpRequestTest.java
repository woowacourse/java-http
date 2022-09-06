package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HttpRequest 클래스의")
class HttpRequestTest {

    @Test
    @DisplayName("생성자는 BufferedReader를 사용하여 객체를 생성한다.")
    void success() {
        // given
        final String request = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        // when
        final HttpRequest httpRequest = HttpRequest.from(bufferedReader);

        // then
        assertAll(
                () -> assertThat(httpRequest.getRequestLine().getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpRequest.getRequestLine().getRequestUri().getPath()).isEqualTo("/index.html"),
                () -> assertThat(httpRequest.getRequestLine().getRequestUri().getParams()).isEmpty(),
                () -> assertThat(httpRequest.getRequestLine().getVersion()).isEqualTo("HTTP/1.1"),
                () -> assertThat(httpRequest.getRequestHeaders().getHeaders()).contains(
                        entry("Host", "localhost:8080"),
                        entry("Connection", "keep-alive")
                )
        );
    }
}
