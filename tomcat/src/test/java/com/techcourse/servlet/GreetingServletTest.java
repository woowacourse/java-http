package com.techcourse.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpMessageBody;
import org.apache.coyote.http.HttpProtocol;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.line.Method;
import org.apache.coyote.http.request.line.RequestLine;
import org.apache.coyote.http.request.line.Uri;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("대문 페이지 요청 서블릿 테스트")
class GreetingServletTest {

    private final GreetingServlet greetingServlet = new GreetingServlet();

    @DisplayName("홈 요청을 처리할 수 있다")
    @Test
    void doServiceHome() throws IOException {
        // given
        RequestLine requestLine = new RequestLine(Method.GET, new Uri("/"), HttpProtocol.HTTP_11);
        HttpHeaders headers = new HttpHeaders();
        HttpMessageBody body = HttpMessageBody.createEmptyBody();

        HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);
        HttpResponse httpResponse = HttpResponse.createEmptyResponse();

        // when
        greetingServlet.doService(httpRequest, httpResponse);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(httpResponse.resolveHttpMessage()).isEqualTo(expected);
    }
}
