package com.techcourse.servlet;

import static org.apache.coyote.http.HttpFixture.EMPTY_BODY;
import static org.apache.coyote.http.HttpFixture.EMPTY_HEADER;
import static org.apache.coyote.http.HttpFixture.REGISTER_REQUEST_LINE;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HttpHeaderName;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpMessageBody;
import org.apache.coyote.http.HttpProtocol;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.line.Method;
import org.apache.coyote.http.request.line.RequestLine;
import org.apache.coyote.http.request.line.Uri;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.line.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("회원가입 서블릿 테스트")
class RegisterServletTest {

    private final RegisterServlet registerServlet = new RegisterServlet();

    @DisplayName("회원가입 페이지 요청을 처리할 수 있다")
    @Test
    void doServiceRegisterPage() {
        // given
        HttpRequest httpRequest = new HttpRequest(REGISTER_REQUEST_LINE, EMPTY_HEADER, EMPTY_BODY);
        HttpResponse httpResponse = HttpResponse.createEmptyResponse();

        // when
        registerServlet.doService(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("회원가입 요청을 초리할 수 있다")
    @Test
    void doServiceRegister() {
        // given
        RequestLine requestLine = new RequestLine(Method.POST, new Uri("/register"), HttpProtocol.HTTP_11);
        HttpHeaders headers = new HttpHeaders();
        headers.putHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        headers.putHeader(HttpHeaderName.CONTENT_LENGTH, "50");
        HttpMessageBody body = new HttpMessageBody("account=libi&email=libi@test.com&password=password");

        HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);
        HttpResponse httpResponse = HttpResponse.createEmptyResponse();

        // when
        registerServlet.doService(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
    }
}
