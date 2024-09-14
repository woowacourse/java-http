package com.techcourse.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpMessageBody;
import org.apache.coyote.http.HttpProtocol;
import org.apache.coyote.http.request.HttpServletRequest;
import org.apache.coyote.http.request.line.Method;
import org.apache.coyote.http.request.line.RequestLine;
import org.apache.coyote.http.request.line.Uri;
import org.apache.coyote.http.response.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("로그인 페이지 서블릿 테스트")
class LoginServletTest {

    private final LoginServlet loginServlet = new LoginServlet();

    @DisplayName("로그인 페이지 요청을 처리할 수 있다")
    @Test
    void doServiceLoginPage() throws IOException {
        // given
        RequestLine requestLine = new RequestLine(Method.GET, new Uri("/"), HttpProtocol.HTTP_11);
        HttpHeaders headers = new HttpHeaders();
        HttpMessageBody body = HttpMessageBody.createEmptyBody();

        HttpServletRequest httpServletRequest = new HttpServletRequest(requestLine, headers, body);
        HttpServletResponse httpServletResponse = HttpServletResponse.createEmptyResponse();

        // when
        loginServlet.doService(httpServletRequest, httpServletResponse);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 3447 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(httpServletResponse.resolveHttpMessage()).isEqualTo(expected);
    }
}
