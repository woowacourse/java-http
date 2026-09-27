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

    @Test
    void 같은_이름의_쿠키_헤더를_각각_응답한다() {
        HttpResponse response = new HttpResponse();

        response.addHeader("Set-Cookie", "theme=dark");
        response.addHeader("Set-Cookie", "JSESSIONID=session-id");

        String actual = new String(response.toByteArray(), StandardCharsets.UTF_8);

        assertThat(actual)
                .contains("Set-Cookie: theme=dark\r\n")
                .contains("Set-Cookie: JSESSIONID=session-id\r\n");
    }

    @Test
    void 헤더를_설정하면_같은_이름의_기존_값을_교체한다() {
        HttpResponse response = new HttpResponse();

        response.addHeader("Location", "/before.html");
        response.setHeader("Location", "/after.html");

        String actual = new String(response.toByteArray(), StandardCharsets.UTF_8);

        assertThat(actual)
                .contains("Location: /after.html\r\n")
                .doesNotContain("Location: /before.html\r\n");
    }

    @Test
    void 헤더_이름의_대소문자가_달라도_같은_헤더로_다룬다() {
        HttpResponse response = new HttpResponse();

        response.addHeader("set-cookie", "theme=dark");
        response.addHeader("Set-Cookie", "JSESSIONID=session-id");
        response.addHeader("content-length", "wrong");
        response.setBody("hello".getBytes(StandardCharsets.UTF_8));

        String actual = new String(response.toByteArray(), StandardCharsets.UTF_8);

        assertThat(response.hasHeader("SET-COOKIE")).isTrue();
        assertThat(actual)
                .contains("set-cookie: theme=dark\r\n")
                .contains("set-cookie: JSESSIONID=session-id\r\n")
                .contains("Content-Length: 5 \r\n")
                .doesNotContain("content-length: wrong\r\n");
    }
}
