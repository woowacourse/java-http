package org.apache.coyote.http11.httpresponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestMaker;

class HttpResponseTest {

    @DisplayName("잘못된 파일 경로를 입력할 경우 예외를 발생시킨다")
    @Test
    void invalidPath() {
        final String request = String.join("\r\n",
                "GET /ln HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(request);
        HttpResponse httpResponse = new HttpResponse();

        assertThatThrownBy(() -> httpResponse.staticResource("/ln"))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("입력한 값을 정확하게 HttpResponse로 변환한다")
    @Test
    void httpResponseBuilder() {
        final String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(request);
        HttpResponse httpResponse = new HttpResponse();

        httpResponse.found(httpRequest);
        httpResponse.location("/index");
        httpResponse.setCookie("JSESSIONID=abcde");
        httpResponse.contentLength("89");
        httpResponse.contentType("test");
        httpResponse.responseBody("testResponseBody");

        assertThat(httpResponse.getBytes())
                .contains("HTTP/1.1 302 Found".getBytes())
                .contains("Location: /index".getBytes())
                .contains("Set-Cookie: JSESSIONID=abcde".getBytes())
                .contains("Content-Length: 89".getBytes())
                .contains("Content-Type: test".getBytes())
                .contains("testResponseBody".getBytes());
    }
}
