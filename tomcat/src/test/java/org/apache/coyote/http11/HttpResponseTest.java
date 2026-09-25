package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    @DisplayName("상태 줄, 헤더, 본문을 HTTP 응답 형식으로 쓴다")
    void writesHttpResponse() throws Exception {
        final HttpResponse response = new HttpResponse("HTTP/1.1");
        response.setStatus(200, "OK");
        response.setHeader("Content-Type", "text/plain");
        response.setBody("Hello".getBytes(StandardCharsets.UTF_8));
        final ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.write(output);

        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Length: 5",
                "Content-Type: text/plain",
                "",
                "Hello"));
    }
}
