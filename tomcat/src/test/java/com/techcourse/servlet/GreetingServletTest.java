package com.techcourse.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http11.common.HttpHeaders;
import org.apache.coyote.http11.common.HttpMessageBody;
import org.apache.coyote.http11.common.HttpProtocol;
import org.apache.coyote.http11.request.HttpServletRequest;
import org.apache.coyote.http11.request.line.Method;
import org.apache.coyote.http11.request.line.RequestLine;
import org.apache.coyote.http11.request.line.Uri;
import org.apache.coyote.http11.response.HttpServletResponse;
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

        HttpServletRequest httpServletRequest = new HttpServletRequest(requestLine, headers, body);
        HttpServletResponse httpServletResponse = HttpServletResponse.createEmptyResponse();

        // when
        greetingServlet.doService(httpServletRequest, httpServletResponse);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(httpServletResponse.resolveHttpMessage()).isEqualTo(expected);
    }
}
