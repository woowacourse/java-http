package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpParserTest {

    @DisplayName("텍스트로 전달받은 httpRequest를 HttpRequest 객체로 파싱할 수 있다.")
    @Test
    void parseToRequest() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*",
                "",
                "");

        InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());

        // when
        HttpRequest request = HttpParser.parseToRequest(inputStream);

        assertAll(() -> {
            assertThat(request.requestUrl()).isEqualTo("/index.html");
            assertThat(request.method()).isEqualTo("GET");
            assertThat(request.httpVersion()).isEqualTo("HTTP/1.1");
            assertThat(request.getHeaderValue("Host")).isEqualTo("localhost:8080");
            assertThat(request.getHeaderValue("Connection")).isEqualTo("keep-alive");
            assertThat(request.getHeaderValue("Accept")).isEqualTo("*/*");
        });
    }
}
