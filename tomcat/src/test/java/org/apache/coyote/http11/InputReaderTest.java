package org.apache.coyote.http11;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class InputReaderTest {

    private InputReader inputReader;

    @BeforeEach
    void setUp() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        this.inputReader = new InputReader(inputStream);
    }

    @Test
    @DisplayName("요청의 첫번째 라인을 읽을 수 있다.")
    void readRequestLine() {
        String requestLine = inputReader.readRequestLine();

        assertThat(requestLine).isEqualTo("GET /index.html HTTP/1.1 ");
    }

    @Test
    @DisplayName("요청의 헤더를 읽을 수 있다.")
    void readHeaders() {
        List<String> headers = inputReader.readHeaders();

        assertSoftly(softly -> {
            softly.assertThat(headers).hasSize(2);
            softly.assertThat(headers.get(0)).isEqualTo("Host: localhost:8080 ");
            softly.assertThat(headers.get(1)).isEqualTo("Connection: keep-alive ");
        });
    }

    @Test
    @DisplayName("요청의 바디를 읽을 수 있다.")
    void readBody() {
        String body = inputReader.readBody();

        assertThat(body).isEmpty();
    }
}
