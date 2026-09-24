package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void createResponse_success() {
        // given
        final HttpResponse response = new HttpResponse();

        response.setStatus(200, "OK");
        response.addHeader(
                "Content-Type",
                "text/html;charset=utf-8"
        );
        response.setBody("Hello world!");

        // when
        final String result = response.toResponse();

        // then
        assertThat(result)
                .contains("HTTP/1.1 200 OK")
                .contains("content-type: text/html;charset=utf-8")
                .contains("content-length: 12")
                .endsWith("\r\n\r\nHello world!");
    }

    @Test
    void contentLength_utf8ByteLength_success() {
        // given
        final HttpResponse response = new HttpResponse();

        final String body = "안녕하세요";
        final int expectedLength = body
                .getBytes(StandardCharsets.UTF_8)
                .length;

        response.setBody(body);

        // when
        final String result = response.toResponse();

        // then
        assertThat(result)
                .contains("content-length: " + expectedLength);
    }

    @Test
    void sendRedirect_success() {
        // given
        final HttpResponse response = new HttpResponse();

        // when
        response.sendRedirect("/index.html");

        final String result = response.toResponse();

        // then
        assertThat(result)
                .contains("HTTP/1.1 302 Found")
                .contains("location: /index.html")
                .contains("content-length: 0");
    }

    @Test
    void addHeader_success() {
        // given
        final HttpResponse response = new HttpResponse();

        // when
        response.addHeader(
                "Set-Cookie",
                "JSESSIONID=session-id"
        );

        // then
        assertThat(response.getHeader("Set-Cookie"))
                .isEqualTo("JSESSIONID=session-id");
    }
}