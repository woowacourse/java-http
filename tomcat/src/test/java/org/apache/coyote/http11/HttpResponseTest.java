package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void 상태와_헤더와_본문을_작성하면_HTTP_응답에_반영된다() {
        byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        HttpResponse response = new HttpResponse();

        response.setStatus(HttpStatus.OK);
        response.addHeader(
                "Content-Type",
                "text/html;charset=utf-8 "
        );
        response.setBody(body);

        String actual = new String(
                response.toByteArray(),
                StandardCharsets.UTF_8
        );

        assertThat(actual).startsWith("HTTP/1.1 200 OK");
        assertThat(actual)
                .contains("Content-Type: text/html;charset=utf-8");
        assertThat(actual)
                .contains("Content-Length: 12");
        assertThat(actual)
                .endsWith("\r\n\r\nHello world!");
    }

    @Test
    void 리다이렉트로_설정하면_302_상태와_Location을_포함한다() {
        HttpResponse response = new HttpResponse();

        response.sendRedirect("/index.html");

        String actual = new String(
                response.toByteArray(),
                StandardCharsets.UTF_8
        );

        assertThat(actual).startsWith("HTTP/1.1 302 FOUND");
        assertThat(actual)
                .contains("Location: /index.html");
    }
}
