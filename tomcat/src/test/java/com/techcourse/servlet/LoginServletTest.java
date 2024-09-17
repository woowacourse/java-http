package com.techcourse.servlet;

import static org.apache.coyote.http.HttpFixture.EMPTY_BODY;
import static org.apache.coyote.http.HttpFixture.EMPTY_HEADER;
import static org.apache.coyote.http.HttpFixture.LOGIN_PAGE_REQUEST_LINE;
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

@DisplayName("로그인 서블릿 테스트")
class LoginServletTest {

    private final LoginServlet loginServlet = new LoginServlet();

    @DisplayName("로그인 페이지 요청을 처리할 수 있다")
    @Test
    void doServiceLoginPage() {
        // given
        HttpRequest httpRequest = new HttpRequest(LOGIN_PAGE_REQUEST_LINE, EMPTY_HEADER, EMPTY_BODY);
        HttpResponse httpResponse = HttpResponse.createEmptyResponse();

        // when
        loginServlet.doService(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("로그인 요청을 처리할 수 있다")
    @Test
    void doServiceLogin() {
        // given
        RequestLine requestLine = new RequestLine(Method.POST, new Uri("/login"), HttpProtocol.HTTP_11);
        HttpHeaders headers = new HttpHeaders();
        headers.putHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        headers.putHeader(HttpHeaderName.CONTENT_LENGTH, "30");
        HttpMessageBody body = new HttpMessageBody("account=gugu&password=password");

        HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);
        HttpResponse httpResponse = HttpResponse.createEmptyResponse();

        // when
        loginServlet.doService(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
    }

    @DisplayName("로그인에 실패하면 401 상태코드롤 응답한다")
    @Test
    void doServiceLoginWithUnauthorized() {
        // given
        RequestLine requestLine = new RequestLine(Method.POST, new Uri("/login"), HttpProtocol.HTTP_11);
        HttpHeaders headers = new HttpHeaders();
        headers.putHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded");
        headers.putHeader(HttpHeaderName.CONTENT_LENGTH, "30");
        HttpMessageBody body = new HttpMessageBody("account=noOne&password=password");

        HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);
        HttpResponse httpResponse = HttpResponse.createEmptyResponse();

        // when
        loginServlet.doService(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
