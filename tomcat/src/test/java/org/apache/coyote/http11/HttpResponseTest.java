package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void 상태_코드와_헤더와_본문으로_HTTP_응답_메시지를_만든다() {
        // given
        byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        var response = new HttpResponse("302 FOUND", "text/html;charset=utf-8 ", body);
        response.addHeader("Location", "/index.html");

        // when
        byte[] actual = response.toByteArray();

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 FOUND ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "Location: /index.html",
                "",
                "Hello world!");
        assertThat(actual).isEqualTo(expected.getBytes(StandardCharsets.UTF_8));
    }
}
