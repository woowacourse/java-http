package org.apache.coyote.http11.request;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.constants.HttpMethod;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("GET 요청시 body는 null 로 httpRequest를 만든다")
    @Test
    void generateGETHttpRequest() throws IOException {
        String httpRequest = """
                GET /test HTTP/1.1
                Content-Type: "text/html"
                Cookie: test cookie
                Accept: text/html

                test body
                """;

        BufferedReader bufferedReader = new  BufferedReader(new StringReader(httpRequest));
        final HttpRequest from = HttpRequest.from(bufferedReader);

        assertAll(
                () -> Assertions.assertThat(from.getMethod()).isEqualTo(HttpMethod.GET),
                () -> Assertions.assertThat(from.getUri()).isEqualTo("/test"),
                () -> Assertions.assertThat(from.getAcceptLine()).isEqualTo("text/html"),
                () -> Assertions.assertThat(from.getCookie()).isEqualTo(new Cookie("test cookie")),
                () -> Assertions.assertThat(from.getVersion()).isEqualTo("HTTP/1.1"),
                () -> Assertions.assertThat(from.getBody()).isEqualTo(null)
        );
    }

    @DisplayName("POST 요청이면 바디도 포함한다")
    @Test
    void generateHttpRequest() throws IOException {
        String httpRequest = """
                POST /test HTTP/1.1
                Content-Length: 28
                
                account=a&password=b&email=c
                """;

        BufferedReader bufferedReader = new  BufferedReader(new StringReader(httpRequest));
        final HttpRequest from = HttpRequest.from(bufferedReader);

        assertAll(
                () -> Assertions.assertThat(from.getMethod()).isEqualTo(HttpMethod.POST),
                () -> Assertions.assertThat(from.getUri()).isEqualTo("/test"),
                () -> Assertions.assertThat(from.getBody()).isEqualTo("account=a&password=b&email=c")
        );
    }

    @DisplayName("쿠키를 가져온다")
    @Test
    void getCookie() throws IOException {
        String httpRequest = """
                GET /test HTTP/1.1
                Content-Length: 28
                Cookie: test cookie
                """;

        BufferedReader bufferedReader = new  BufferedReader(new StringReader(httpRequest));
        final HttpRequest from = HttpRequest.from(bufferedReader);

        Assertions.assertThat(from.getCookie()).isEqualTo(new Cookie("test cookie"));
    }

    @DisplayName("요청에 쿠키가 없으면 null을 반환한다.")
    @Test
    void getNullWhenNoCookie() throws IOException {
        String httpRequest = """
                GET /test HTTP/1.1
                Content-Length: 28
                """;

        BufferedReader bufferedReader = new  BufferedReader(new StringReader(httpRequest));
        final HttpRequest from = HttpRequest.from(bufferedReader);

        Assertions.assertThat(from.getCookie()).isNull();
    }
}
