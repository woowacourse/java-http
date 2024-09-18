package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.cookie.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @DisplayName("정적 리소스 응답을 생성한다.")
    @Test
    void setStaticResourceResponse() throws Exception {
        HttpResponse httpResponse = new HttpResponse();

        httpResponse.setStaticResourceResponse("/index.html");

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                       "Content-Type: text/html;charset=utf-8 \r\n" +
                       "Content-Length: 5564 \r\n" +
                       "\r\n" +
                       new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(httpResponse.getResponse()).isEqualToIgnoringWhitespace(expected);
    }

    @DisplayName("리다이렉트 응답을 생성한다.")
    @Test
    void setRedirectResponse() throws IOException {
        HttpResponse httpResponse = new HttpResponse();

        httpResponse.setRedirectResponse("/index.html");
        var expected = "HTTP/1.1 302 Found \r\n" +
                       "Location: /index.html \r\n" +
                       "\r\n";

        assertThat(httpResponse.getResponse()).isEqualToIgnoringWhitespace(expected);
    }

    @DisplayName("응답 헤더로 보낼 바디를 세팅한다.")
    @Test
    void setBody() throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        ResponseBody body = new ResponseBody("text/html", "Hello world!");

        httpResponse.setBody(body);
        httpResponse.set200Response();

        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(httpResponse.getResponse()).isEqualToIgnoringWhitespace(expected);
    }

    @DisplayName("응답 헤더로 보낼 쿠키를 세팅한다.")
    @Test
    void setCookie() throws IOException {
        HttpResponse httpResponse = new HttpResponse();
        Cookie cookie = new Cookie("cookie", "very-tasty");

        httpResponse.setCookie(cookie);
        httpResponse.set200Response();

        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Set-Cookie: cookie=very-tasty ",
                "");

        assertThat(httpResponse.getResponse()).isEqualToIgnoringWhitespace(expected);
    }
}
