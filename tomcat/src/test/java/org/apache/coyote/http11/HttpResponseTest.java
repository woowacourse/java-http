package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void 본문을_설정하면_200_응답과_컨텐츠_헤더를_만든다() {
        HttpResponse response = new HttpResponse();

        response.setBody("text/html", "Hello world!");

        assertThat(toString(response)).isEqualTo(String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"));
    }

    @Test
    void 리다이렉트하면_302_응답과_Location_헤더를_만든다() {
        HttpResponse response = new HttpResponse();

        response.sendRedirect("/index.html");
        response.addCookie("JSESSIONID", "abc");

        assertThat(toString(response)).isEqualTo(String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Set-Cookie: JSESSIONID=abc ",
                "Location: /index.html ",
                "",
                ""));
    }

    private String toString(HttpResponse response) {
        return new String(response.getBytes(), StandardCharsets.UTF_8);
    }
}
