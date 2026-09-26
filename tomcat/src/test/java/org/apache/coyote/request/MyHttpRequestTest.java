package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import org.junit.jupiter.api.Test;

class MyHttpRequestTest {

    @Test
    void percent_encoding된_폼_파라미터를_디코딩한다() {
        MyHttpRequest request = requestWithBody("email=user%40example.com&nickname=hello%20world");

        assertThat(request.getFormParameters())
                .containsEntry("email", "user@example.com")
                .containsEntry("nickname", "hello world");
    }

    @Test
    void 플러스는_공백으로_디코딩한다() {
        MyHttpRequest request = requestWithBody("nickname=hello+world");

        assertThat(request.getFormParameters()).containsEntry("nickname", "hello world");
    }

    @Test
    void UTF_8로_인코딩된_폼_파라미터를_디코딩한다() {
        String encodedValue = URLEncoder.encode("홍길동", StandardCharsets.UTF_8);
        MyHttpRequest request = requestWithBody("name=" + encodedValue);

        assertThat(request.getFormParameters()).containsEntry("name", "홍길동");
    }

    @Test
    void 값에_포함된_인코딩된_등호를_보존한다() {
        MyHttpRequest request = requestWithBody("password=a%3Db");

        assertThat(request.getFormParameters()).containsEntry("password", "a=b");
    }

    @Test
    void Accept_헤더가_중복되면_대소문자와_순서를_유지해_결합한다() {
        String rawRequest = String.join("\r\n",
                "GET / HTTP/1.1",
                "accept: text/html",
                "Accept: text/plain",
                "",
                "");

        MyHttpRequest request = HttpRequestParser.parse(rawRequest);

        assertThat(request.getHeader("Accept")).hasValue("text/html, text/plain");
    }

    private MyHttpRequest requestWithBody(String body) {
        String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);
        return HttpRequestParser.parse(rawRequest);
    }
}
