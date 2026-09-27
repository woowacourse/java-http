package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.session.Session;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void ok_응답은_200과_컨텐츠_헤더를_만든다() throws IOException {
        HttpResponse response = HttpResponse.empty();

        response.ok(ContentType.HTML, "Hello world!");

        assertThat(toString(response)).isEqualTo(String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"));
    }

    @Test
    void redirect_응답은_302와_Location_헤더를_만든다() throws IOException {
        HttpResponse response = HttpResponse.empty();

        response.redirect("/index.html");

        assertThat(toString(response)).isEqualTo(String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "",
                ""));
    }

    @Test
    void 새_세션으로_만든_응답은_세션_쿠키를_발급한다() throws IOException {
        HttpResponse response = HttpResponse.from(new Session("abc"));

        assertThat(toString(response)).contains("Set-Cookie: JSESSIONID=abc ");
    }

    @Test
    void 기존_세션으로_만든_응답은_세션_쿠키를_발급하지_않는다() throws IOException {
        Session session = new Session("abc");
        session.access();

        HttpResponse response = HttpResponse.from(session);

        assertThat(toString(response)).doesNotContain("Set-Cookie");
    }

    private String toString(HttpResponse response) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.writeTo(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
