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
        HttpStatusCode statusCode = HttpStatusCode.OK;

        // when
        HttpResponse httpResponse = new HttpResponse(statusCode);

        // then
        assertThat(httpResponse.getStatusCode()).isEqualTo("200 OK");
    }

    @Test
    void 리다이렉션을_설정할_수_있다() {
        // given
        HttpResponse httpResponse = new HttpResponse(HttpStatusCode.FOUND);

        // when
        httpResponse.setLocation("/new-location");

        // then
        Map<String, String> headers = httpResponse.getHeader();
        assertThat(headers.get("Location")).isEqualTo("/new-location");
        assertThat(httpResponse.getContentLength()).isEqualTo(0);
    }

    @Test
    void 쿠키를_설정할_수_있다() {
        // given
        HttpResponse httpResponse = new HttpResponse(HttpStatusCode.OK);
        HttpCookie cookie = new HttpCookie("sessionId", "abc123");

        // when
        httpResponse.setCookie(cookie);

        // then
        Map<String, String> headers = httpResponse.getHeader();
        assertThat(headers.get("Set-Cookie")).isEqualTo("sessionId=abc123");
    }

    @Test
    void 콘텐츠를_설정할_수_있다() {
        // given
        HttpResponse httpResponse = new HttpResponse(HttpStatusCode.OK);
        String path = "/index.html";
        String content = "<html><body>Hello World</body></html>";

        // when
        httpResponse.setContent(path, content);

        // then
        Map<String, String> headers = httpResponse.getHeader();
        assertThat(headers.get("Content-Type")).isEqualTo("text/html;charset=utf-8");
        assertThat(httpResponse.getContentLength()).isEqualTo(content.getBytes().length);
        assertThat(httpResponse.getBody()).isEqualTo(content);
    }

    @Test
    void 헤더를_파싱할_수_있다() {
        // given
        HttpResponse httpResponse = new HttpResponse(HttpStatusCode.OK);
        HttpCookie cookie = new HttpCookie("sessionId", "abc123");
        String path = "/index.html";
        String content = "<html><body>Hello World</body></html>";

        // when
        httpResponse.setCookie(cookie);
        httpResponse.setContent(path, content);

        // then
        Map<String, String> headers = httpResponse.getHeader();
        assertThat(headers.size()).isEqualTo(3);
        assertThat(headers.get("Set-Cookie")).isEqualTo("sessionId=abc123");
        assertThat(headers.get("Content-Type")).isEqualTo("text/html;charset=utf-8");
        assertThat(headers.get("Content-Length")).isEqualTo(String.valueOf(content.getBytes().length));
    }
}
