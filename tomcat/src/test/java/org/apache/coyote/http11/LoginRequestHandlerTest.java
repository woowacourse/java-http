package org.apache.coyote.http11;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.handler.LoginRequestHandler;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Http11RequestBody;
import org.apache.coyote.http11.request.Http11RequestHeaders;
import org.apache.coyote.http11.request.Http11RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginRequestHandlerTest {

    private RequestHandler handler = new LoginRequestHandler();

    @DisplayName("/login POST 요청이면 핸들링 가능하다.")
    @Test
    void canHandling1() {
        String body = "account=gugu&password=password";
        Http11RequestLine requestLine = new Http11RequestLine("POST /login HTTP/1.1");
        Http11RequestHeaders headers = new Http11RequestHeaders(
                List.of("Content-Length: " + body.getBytes().length));
        Http11RequestBody requestBody = new Http11RequestBody(body);
        Http11Request request = new Http11Request(requestLine, headers, requestBody);

        assertThat(handler.canHandling(request)).isTrue();
    }

    @DisplayName("/login GET 요청이면 핸들링 가능하다.")
    @Test
    void canHandling() {
        Http11RequestLine requestLine = new Http11RequestLine("GET /login HTTP/1.1");
        Http11RequestHeaders headers = new Http11RequestHeaders(Collections.emptyList());
        Http11RequestBody requestBody = new Http11RequestBody("");
        Http11Request request = new Http11Request(requestLine, headers, requestBody);

        assertThat(handler.canHandling(request)).isTrue();
    }

    @DisplayName("로그인을 하면 /index.html로 리다이렉트 한다.")
    @Test
    void handle1() {
        String body = "account=gugu&password=password";
        Http11RequestLine requestLine = new Http11RequestLine("POST /login HTTP/1.1");
        Http11RequestHeaders headers = new Http11RequestHeaders(
                List.of("Content-Length: " + body.getBytes().length));
        Http11RequestBody requestBody = new Http11RequestBody(body);
        Http11Request request = new Http11Request(requestLine, headers, requestBody);

        String actual = handler.handle(request).getResponseMessage();
        String expectedResponseLine = "HTTP/1.1 302 Found";
        String expectedLocation = "/index.html";

        assertThat(actual).contains(expectedResponseLine);
        assertThat(actual).contains(expectedLocation);
    }

    @DisplayName("쿠키에 세션 id가 존재하면, /login GET 요청 시 /index.html로 리다이렉트 된다.")
    @Test
    void handle2() {
        Http11RequestLine requestLine = new Http11RequestLine("GET /login HTTP/1.1");
        Http11RequestHeaders headers = new Http11RequestHeaders(
                List.of("Cookie: JSESSIONID=123")
        );
        Http11RequestBody requestBody = new Http11RequestBody("");
        Http11Request request = new Http11Request(requestLine, headers, requestBody);

        String actual = handler.handle(request).getResponseMessage();
        String expectedResponseLine = "HTTP/1.1 302 Found";
        String expectedLocation = "/index.html";

        assertThat(actual).contains(expectedResponseLine);
        assertThat(actual).contains(expectedLocation);
    }
}
