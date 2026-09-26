package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpMethod;
import org.apache.coyote.http11.enums.HttpStatus;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseWriterTest {

    @Test
    void 상태_라인과_헤더와_본문을_HTTP_응답으로_직렬화한다() {
        final HttpRequest request = HttpRequest.builder()
                .httpMethod(HttpMethod.GET)
                .path("/index.html")
                .version("HTTP/1.1")
                .build();
        final HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.SEE_OTHER);
        response.addHeader("Location", "/login");
        response.setBody("hello".getBytes(StandardCharsets.UTF_8));

        final String serializedResponse = new HttpResponseWriter().write(request, response);

        assertThat(serializedResponse)
                .startsWith("HTTP/1.1 303 See Other \r\n")
                .contains("Location: /login \r\n")
                .contains("Content-Type: text/html;charset=utf-8 \r\n")
                .contains("Content-Length: 5 \r\n")
                .endsWith("\r\nhello");
    }
}
