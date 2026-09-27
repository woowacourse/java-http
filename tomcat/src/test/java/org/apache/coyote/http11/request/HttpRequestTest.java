package org.apache.coyote.http11.request;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void JSON_본문은_폼_파라미터로_파싱하지_않는다() throws IOException {
        // given
        final var body = "{\"name\":\"juni\"}";
        final var requestMessage = String.join("\r\n",
                "POST /items HTTP/1.1",
                "Content-Type: application/json",
                "Content-Length: " + body.length(),
                "",
                body
        );
        final var inputStream = new ByteArrayInputStream(
                requestMessage.getBytes(StandardCharsets.US_ASCII)
        );

        // when
        final var request = new HttpRequest(inputStream);

        // then
        assertThat(request.getBody()).isEqualTo(body);
        assertThat(request.getParameter("name")).isNull();
    }
}
