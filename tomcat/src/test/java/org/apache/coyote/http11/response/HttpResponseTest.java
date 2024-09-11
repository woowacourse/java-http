package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.cookie.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class HttpResponseTest {

    @DisplayName("정적 리소스 응답을 보낸다.")
    @Test
    void sendStaticResourceResponse() throws Exception {
        StubSocket socket = new StubSocket();
        HttpResponse httpResponse = new HttpResponse(socket.getOutputStream());

        httpResponse.sendStaticResourceResponse("/index.html");

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                       "Content-Type: text/html;charset=utf-8 \r\n" +
                       "Content-Length: 5564 \r\n" +
                       "\r\n" +
                       new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualToIgnoringWhitespace(expected);
    }

    @DisplayName("리다이렉트 응답을 보낸다.")
    @Test
    void sendRedirect() throws IOException {
        StubSocket socket = new StubSocket();
        HttpResponse httpResponse = new HttpResponse(socket.getOutputStream());

        httpResponse.sendRedirect("/index.html");
        var expected = "HTTP/1.1 302 Found \r\n" +
                       "Location: /index.html \r\n" +
                       "\r\n";

        assertThat(socket.output()).isEqualToIgnoringWhitespace(expected);
    }

    @DisplayName("응답 헤더로 보낼 바디를 세팅한다.")
    @Test
    void setBody() throws IOException {
        StubSocket socket = new StubSocket();
        HttpResponse httpResponse = new HttpResponse(socket.getOutputStream());
        ResponseBody body = new ResponseBody("text/html", "Hello world!");

        httpResponse.setBody(body);
        httpResponse.send200Response();

        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualToIgnoringWhitespace(expected);
    }

    @DisplayName("응답 헤더로 보낼 쿠키를 세팅한다.")
    @Test
    void setCookie() throws IOException {
        StubSocket socket = new StubSocket();
        HttpResponse httpResponse = new HttpResponse(socket.getOutputStream());
        Cookie cookie = new Cookie("cookie", "very-tasty");

        httpResponse.setCookie(cookie);
        httpResponse.send200Response();

        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Set-Cookie: cookie=very-tasty ",
                "");

        assertThat(socket.output()).isEqualToIgnoringWhitespace(expected);
    }
}
