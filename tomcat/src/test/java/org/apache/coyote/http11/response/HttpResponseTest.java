package org.apache.coyote.http11.response;

import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.coyote.http11.session.HttpCookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    @DisplayName("200 응답은 Content-Type과 Content-Length를 포함한다.")
    void ok() throws IOException {
        // given
        String body = "<h1>Hello</h1>";
        HttpResponse response = HttpResponse.ok(ContentType.HTML, body);

        // when
        String actual = write(response);

        // then
        assertThat(actual).isEqualTo(String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body));
    }

    @Test
    @DisplayName("전달한 ContentType이 헤더에 들어간다.")
    void okWithContentType() throws IOException {
        // given
        HttpResponse response = HttpResponse.ok(ContentType.CSS, "body {}");

        // when
        String actual = write(response);

        // then
        assertThat(actual).contains("Content-Type: text/css;charset=utf-8");
    }

    @Test
    @DisplayName("Content-Length는 문자 수가 아닌 바이트 수다.")
    void contentLengthIsByteLength() throws IOException {
        // given
        String body = "안녕";
        HttpResponse response = HttpResponse.ok(ContentType.HTML, body);

        // when
        String actual = write(response);

        // then
        assertThat(actual).contains("Content-Length: 6");
    }

    @Test
    @DisplayName("404 응답은 html 타입으로 내려간다.")
    void notFound() throws IOException {
        // given
        String body = "<h1>404 Not Found</h1>";
        HttpResponse response = HttpResponse.notFound(body);

        // when
        String actual = write(response);

        // then
        assertThat(actual).isEqualTo(String.join("\r\n",
                "HTTP/1.1 404 Not Found",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body));
    }

    @Test
    @DisplayName("리다이렉트 응답은 Location을 포함하고 바디가 없다.")
    void redirect() throws IOException {
        // given
        HttpResponse response = HttpResponse.redirect("/index.html");

        // when
        String actual = write(response);

        // then
        assertThat(actual).isEqualTo(String.join("\r\n",
                "HTTP/1.1 302 FOUND",
                "Location: /index.html",
                "Content-Length: 0",
                ""));
    }

    @Test
    @DisplayName("쿠키와 함께 리다이렉트하면 Set-Cookie가 추가된다.")
    void redirectWithCookie() throws IOException {
        // given
        HttpCookie cookie = new HttpCookie("JSESSIONID=abc123");
        HttpResponse response = HttpResponse.redirect("/index.html", cookie);

        // when
        String actual = write(response);

        // then
        assertThat(actual).isEqualTo(String.join("\r\n",
                "HTTP/1.1 302 FOUND",
                "Location: /index.html",
                "Set-Cookie: JSESSIONID=abc123",
                "Content-Length: 0",
                ""));
    }

    private String write(HttpResponse response) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.write(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }

}
