package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void 본문이_있으면_Content_Length를_함께_응답한다() throws IOException {
        final HttpResponse response = new HttpResponse();
        response.setBody("text/html;charset=utf-8", "Hello world!");

        assertThat(write(response)).isEqualTo(String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"));
    }

    @Test
    void 리다이렉트_응답은_302와_Location_헤더를_가진다() throws IOException {
        final HttpResponse response = new HttpResponse();
        response.sendRedirect("/index.html");

        assertThat(write(response)).isEqualTo("HTTP/1.1 302 Found \r\nLocation: /index.html \r\n\r\n");
    }

    @Test
    void 쿠키를_Set_Cookie_헤더로_응답한다() throws IOException {
        final HttpResponse response = new HttpResponse();
        response.addCookie("JSESSIONID", "abc");
        response.sendRedirect("/index.html");

        assertThat(write(response)).contains("Set-Cookie: JSESSIONID=abc ");
    }

    @Test
    void 정적_리소스의_Content_Type을_확장자로_결정한다() throws IOException {
        final HttpResponse response = new HttpResponse();
        response.sendStaticResource("/css/styles.css");

        assertThat(write(response)).contains("HTTP/1.1 200 OK", "Content-Type: text/css;charset=utf-8");
    }

    @Test
    void 존재하지_않는_리소스는_404를_응답한다() throws IOException {
        final HttpResponse response = new HttpResponse();
        response.sendStaticResource("/not-exists.html");

        assertThat(write(response)).startsWith("HTTP/1.1 404 Not Found ");
    }

    @Test
    void 상위_경로_접근은_404를_응답한다() throws IOException {
        final HttpResponse response = new HttpResponse();
        response.sendStaticResource("/../application.yml");

        assertThat(write(response)).startsWith("HTTP/1.1 404 Not Found ");
    }

    private String write(final HttpResponse response) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.write(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
