package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void 상태와_헤더와_본문으로_HTTP_응답을_만든다() {
        byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        HttpResponse response =
                new HttpResponse("200 OK", "text/html;charset=utf-8 ", body);

        byte[] actual = response.toByteArray();

        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );

        assertThat(actual)
                .isEqualTo(expected.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void 리다이렉트_응답에_302_상태와_Location을_포함한다() {
        HttpResponse response = HttpResponse.redirectTo("/index.html");

        String actual = new String(
                response.toByteArray(),
                StandardCharsets.UTF_8
        );

        assertThat(actual).startsWith("HTTP/1.1 302 FOUND");
        assertThat(actual).contains("Location: /index.html");
    }
}
