package org.apache.coyote.http.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpStatusCode;
import org.junit.jupiter.api.Test;

public class HttpResponseTest {

    @Test
    void 상태코드를_설정할_수_있다() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.setStatusCode(HttpStatusCode.OK);

        // then
        assertThat(response.getStatusCode()).isEqualTo("200 OK");
    }

    @Test
    void 리다이렉션을_설정할_수_있다() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.setLocation("/new-location");

        // then
        Map<String, String> headers = response.getHeader();
        assertThat(headers.get("Location")).isEqualTo("/new-location");
        assertThat(response.getContentLength()).isEqualTo(0);
    }

    @Test
    void 쿠키를_설정할_수_있다() {
        // given
        HttpResponse response = new HttpResponse();
        HttpCookie cookie = new HttpCookie("sessionId", "abc123");

        // when
        response.setCookie(cookie);

        // then
        Map<String, String> headers = response.getHeader();
        assertThat(headers.get("Set-Cookie")).isEqualTo("sessionId=abc123");
    }

    @Test
    void 콘텐츠를_설정할_수_있다() {
        // given
        HttpResponse response = new HttpResponse();
        String path = "/index.html";
        String content = "<html><body>Hello World</body></html>";

        // when
        response.setContent(path, content);

        // then
        Map<String, String> headers = response.getHeader();
        assertThat(headers.get("Content-Type")).isEqualTo("text/html;charset=utf-8");
        assertThat(response.getContentLength()).isEqualTo(content.getBytes().length);
        assertThat(response.getBody()).isEqualTo(content);
    }

    @Test
    void 헤더를_파싱할_수_있다() {
        // given
        HttpResponse response = new HttpResponse();
        HttpCookie cookie = new HttpCookie("sessionId", "abc123");
        String path = "/index.html";
        String content = "<html><body>Hello World</body></html>";

        // when
        response.setCookie(cookie);
        response.setContent(path, content);

        // then
        Map<String, String> headers = response.getHeader();
        assertThat(headers.size()).isEqualTo(3);
        assertThat(headers.get("Set-Cookie")).isEqualTo("sessionId=abc123");
        assertThat(headers.get("Content-Type")).isEqualTo("text/html;charset=utf-8");
        assertThat(headers.get("Content-Length")).isEqualTo(String.valueOf(content.getBytes().length));
    }
}
