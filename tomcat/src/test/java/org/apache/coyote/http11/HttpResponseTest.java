package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    void 성공_응답을_생성한다() {
        // given
        Map<String, String> headers = Map.of(
                "Set-Cookie", "JSESSIONID=session-id; Path=/"
        );

        // when
        HttpResponse response = HttpResponse.createSuccessResponse(
                headers,
                "text/plain;charset=utf-8",
                "Hello world!"
        );
        String message = response.toResponse();

        // then
        assertThat(message).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(message).contains("Content-Type: text/plain;charset=utf-8\r\n");
        assertThat(message).contains("Content-Length: 12\r\n");
        assertThat(message).contains("Set-Cookie: JSESSIONID=session-id; Path=/\r\n");
        assertThat(responseBody(message)).isEqualTo("Hello world!");
    }

    @Test
    void 리다이렉트_응답을_생성한다() {
        // when
        HttpResponse response = HttpResponse.createRedirectResponse(
                Map.of(),
                "/index.html"
        );
        String message = response.toResponse();

        // then
        assertThat(message).startsWith("HTTP/1.1 302 Found\r\n");
        assertThat(message).contains("Location: /index.html\r\n");
        assertThat(message).contains("Content-Length: 0\r\n");
        assertThat(responseBody(message)).isEmpty();
    }

    @Test
    void 찾을_수_없음_응답을_생성한다() {
        // when
        HttpResponse response = HttpResponse.createNotFoundResponse(Map.of());
        String message = response.toResponse();

        // then
        assertThat(message).startsWith("HTTP/1.1 404 Not Found\r\n");
        assertThat(message).contains("Content-Type: text/plain;charset=utf-8\r\n");
        assertThat(message).contains("Content-Length: 9\r\n");
        assertThat(responseBody(message)).isEqualTo("Not Found");
    }

    private static String responseBody(String response) {
        return response.substring(response.indexOf("\r\n\r\n") + 4);
    }
}
