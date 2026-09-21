package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpResponseTest {

    @Test
    void writesStatusLineHeadersAndBody() throws Exception {
        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Set-Cookie", "JSESSIONID=session-id");
        headers.put("Content-Type", "text/plain;charset=utf-8");
        HttpResponse response = new HttpResponse(
                HttpStatus.OK,
                headers,
                "안녕"
        );
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        response.writeTo(outputStream);

        assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 200 OK\r\n"
                        + "Set-Cookie: JSESSIONID=session-id\r\n"
                        + "Content-Type: text/plain;charset=utf-8\r\n"
                        + "Content-Length: 6\r\n"
                        + "\r\n"
                        + "안녕"
        );
    }

    @Test
    void rejectsCallerProvidedContentLength() {
        assertThatThrownBy(() -> new HttpResponse(
                HttpStatus.OK,
                Map.of("content-length", "1"),
                "body"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Content-Length는 응답 본문으로부터 계산됩니다.");
    }

    @Test
    void writesErrorResponse() throws Exception {
        HttpResponse response = new HttpResponse();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        response.sendError(HttpStatus.NOT_FOUND, "Not Found");
        response.writeTo(outputStream);

        assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 404 Not Found\r\n"
                        + "Content-Type: text/plain;charset=utf-8\r\n"
                        + "Content-Length: 9\r\n"
                        + "\r\n"
                        + "Not Found"
        );
    }
}
