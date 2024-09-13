package org.apache.catalina.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractHttpServletTest {

    static class TestHttpServlet extends AbstractHttpServlet {
        @Override
        protected void doGet(HttpRequest request, HttpResponse response) {
            response.setStatusCode(StatusCode.OK);
        }
    }

    @Test
    @DisplayName("구현되지 않은 메서드 호출 시 501 응답을 반환한다.")
    void methodNotImplemented() {
        byte[] requestBytes = "TRACE / HTTP/1.1\r\n".getBytes();
        AbstractHttpServlet servlet = new TestHttpServlet();
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(requestBytes));
        HttpResponse response = new HttpResponse();

        servlet.service(request, response);
        assertThat(response.getStatusCode()).isEqualTo(StatusCode.NOT_IMPLEMENTED);
    }

    @Test
    @DisplayName("메서드는 지원하나 오버라이드하지 않았다면 405 응답을 반환한다.")
    void methodNotAllowed() {
        byte[] requestBytes = "POST / HTTP/1.1\r\n".getBytes();
        AbstractHttpServlet servlet = new TestHttpServlet();
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(requestBytes));
        HttpResponse response = new HttpResponse();

        servlet.service(request, response);
        assertThat(response.getStatusCode()).isEqualTo(StatusCode.METHOD_NOT_ALLOWED);
    }

    @Test
    @DisplayName("매핑된 메서드를 찾아 요청을 처리한다.")
    void handleMethod() {
        byte[] requestBytes = "GET / HTTP/1.1\r\n".getBytes();
        AbstractHttpServlet servlet = new TestHttpServlet();
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(requestBytes));
        HttpResponse response = new HttpResponse();

        servlet.service(request, response);
        assertThat(response.getStatusCode()).isEqualTo(StatusCode.OK);
    }
}
