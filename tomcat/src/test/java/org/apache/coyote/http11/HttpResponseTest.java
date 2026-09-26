package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static support.HttpResponseFixtures.responseText;

class HttpResponseTest {

    @Test
    void 성공_응답을_생성한다() {
        // given
        HttpResponse response = new HttpResponse();
        response.addHeader("Set-Cookie", "JSESSIONID=session-id; Path=/");

        // when
        response.ok(
                "text/plain;charset=utf-8",
                "Hello world!"
        );
        String message = responseText(response);

        // then
        assertThat(message).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(message).contains("Content-Type: text/plain;charset=utf-8\r\n");
        assertThat(message).contains("Content-Length: 12\r\n");
        assertThat(message).contains("Set-Cookie: JSESSIONID=session-id; Path=/\r\n");
        assertThat(responseBody(message)).isEqualTo("Hello world!");
    }

    @Test
    void 리다이렉트_응답을_생성한다() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.sendRedirect("/index.html");
        String message = responseText(response);

        // then
        assertThat(message).startsWith("HTTP/1.1 302 Found\r\n");
        assertThat(message).contains("Location: /index.html\r\n");
        assertThat(message).contains("Content-Length: 0\r\n");
        assertThat(responseBody(message)).isEmpty();
    }

    @Test
    void 찾을_수_없음_응답을_생성한다() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.notFound();
        String message = responseText(response);

        // then
        assertThat(message).startsWith("HTTP/1.1 404 Not Found\r\n");
        assertThat(message).contains("Content-Type: text/plain;charset=utf-8\r\n");
        assertThat(message).contains("Content-Length: 9\r\n");
        assertThat(responseBody(message)).isEqualTo("Not Found");
    }

    @Test
    void 응답_상태가_설정되지_않으면_예외가_발생한다() {
        // given
        HttpResponse response = new HttpResponse();

        // when & then
        assertThatThrownBy(response::toBytes)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("HTTP 응답 상태가 설정되지 않았습니다.");
    }

    @Test
    void 바이너리_본문의_원본_바이트를_유지한다() {
        // given
        HttpResponse response = new HttpResponse();
        byte[] body = {(byte) 0xff, 0x00, (byte) 0x80};

        // when
        response.ok("application/octet-stream", body);

        // then
        assertThat(response.toBytes()).endsWith(body);
    }

    private static String responseBody(String response) {
        return response.substring(response.indexOf("\r\n\r\n") + 4);
    }
}
