package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void ok_응답은_200과_컨텐츠_헤더를_만든다() {
        HttpResponse response = new HttpResponse();

        response.ok(ContentType.HTML, "Hello world!");

        assertThat(toString(response)).isEqualTo(String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"));
    }

    @Test
    void redirect_응답은_302와_Location_헤더를_만든다() {
        HttpResponse response = new HttpResponse();

        response.redirect("/index.html");
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
