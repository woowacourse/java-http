package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HTTP 응답 출력기")
class HttpResponseWriterTest {

    private final HttpResponseWriter responseWriter = new HttpResponseWriter();

    @Test
    @DisplayName("상태선, 헤더, 본문으로 구성된 HTTP 응답을 출력한다")
    void writesHttpResponse() throws Exception {
        // given
        final var body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        final var response = HttpResponse.ok(new ResponseContent("text/plain;charset=utf-8", body))
                .addHeader("X-Test", "value");
        final var outputStream = new ByteArrayOutputStream();

        // when
        responseWriter.write(response, outputStream);

        // then
        assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo(String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "X-Test: value ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"));
    }
}
