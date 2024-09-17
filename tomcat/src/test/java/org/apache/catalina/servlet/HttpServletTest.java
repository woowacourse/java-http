package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpMessageBody;
import org.apache.coyote.http.HttpProtocol;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.line.Method;
import org.apache.coyote.http.request.line.RequestLine;
import org.apache.coyote.http.request.line.Uri;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.line.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HttpServletTest {

    private HttpServlet httpServlet;
    private HttpRequest getRequest;
    private HttpRequest postRequest;
    private HttpResponse response;

    @BeforeEach
    void setUp() {
        httpServlet = new HttpServlet() {
            @Override
            protected void doGet(HttpRequest request, HttpResponse response) {
                super.doGet(request, response);  // 기본적으로 405 처리
            }

            @Override
            protected void doPost(HttpRequest request, HttpResponse response) {
                super.doPost(request, response);  // 기본적으로 405 처리
            }
        };

        RequestLine getRequestLine = new RequestLine(Method.GET, new Uri("/test"), HttpProtocol.HTTP_11);
        RequestLine postRequestLine = new RequestLine(Method.POST, new Uri("/test"), HttpProtocol.HTTP_11);

        getRequest = new HttpRequest(getRequestLine, new HttpHeaders(), HttpMessageBody.createEmptyBody());
        postRequest = new HttpRequest(postRequestLine, new HttpHeaders(), HttpMessageBody.createEmptyBody());

        response = HttpResponse.createEmptyResponse();
    }

    @Test
    void return405IfNotOverrideDoGet() {
        // given
        httpServlet.doService(getRequest, response);

        // when & then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    void return405IfNotOverrideDoPost() {
        // given
        httpServlet.doService(postRequest, response);

        // when & then
        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }
}
