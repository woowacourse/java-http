package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpResponseTest {

    @Test
    void 상태라인과_Header와_Body를_응답한다()
            throws IOException {
        // given
        final HttpResponse response = new HttpResponse();

        response.setHeader(
                "Content-Type",
                "text/plain;charset=utf-8"
        );

        response.setBody("Hello".getBytes(StandardCharsets.UTF_8));

        final ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        // when
        response.writeTo(outputStream);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/plain;charset=utf-8",
                "Content-Length: 5",
                "",
                "Hello"
        );

        assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo(expected);
    }

    @Test
    void 지정한_경로로_리다이렉트한다() throws IOException {
        // given
        final HttpResponse response = new HttpResponse();

        response.sendRedirect("/index.html");

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        response.writeTo(outputStream);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /index.html",
                "Content-Length: 0",
                "",
                ""
        );

        assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo(expected);
    }

    @Test
    void Cookie를_응답_Header에_추가한다() throws IOException {
        // given
        final HttpResponse response = new HttpResponse();

        response.addCookie("JSESSIONID", "abc");
        response.sendRedirect("/index.html");

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        response.writeTo(outputStream);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Set-Cookie: JSESSIONID=abc",
                "Location: /index.html",
                "Content-Length: 0",
                "",
                ""
        );

        assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo(expected);
    }

    @Test
    void 상태_코드와_응답_본문을_설정한다() throws IOException {
        final HttpResponse response = new HttpResponse();
        response.setStatus(404, "Not Found");
        response.setHeader("Content-Type", "text/plain;charset=utf-8");
        response.setBody("Not Found".getBytes(StandardCharsets.UTF_8));

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        response.writeTo(outputStream);

        final String actual = outputStream.toString(StandardCharsets.UTF_8);

        assertThat(actual).isEqualTo(String.join("\r\n",
                "HTTP/1.1 404 Not Found",
                "Content-Type: text/plain;charset=utf-8",
                "Content-Length: 9",
                "",
                "Not Found"
        ));
    }
}
