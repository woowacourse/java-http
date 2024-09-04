package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    @DisplayName("HttpRequest로 올바르게 파싱한다.")
    void convertRequest() {
        String request = """
                POST /ping HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Content-Length: 26\r
                User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36\r
                \r
                This is Body
                Hello, World!
                """;
        ByteArrayInputStream inputStream = new ByteArrayInputStream(request.getBytes());
        HttpRequest httpRequest = new HttpRequest(inputStream);

        assertAll(
                () -> assertThat(httpRequest.getMethod()).isEqualTo(HttpMethod.POST),
                () -> assertThat(httpRequest.getUri()).hasToString("/ping"),
                () -> assertThat(httpRequest.getBody()).isEqualTo("This is Body\nHello, World!\n")
        );
    }
}
