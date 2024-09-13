package org.apache.catalina.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.StringReader;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.Test;

public class AbstractControllerTest {

    private static class TestController extends AbstractController {

        boolean doGetCalled = false;
        boolean doPostCalled = false;

        @Override
        protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
            doPostCalled = true;
        }

        @Override
        protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
            doGetCalled = true;
        }
    }

    @Test
    public void GET_요청이_들어오면_doGet_메서드를_호출한다() throws Exception {
        // Given
        String httpRequestString = "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(httpRequestString));
        HttpRequest request = new HttpRequest(bufferedReader);
        HttpResponse response = new HttpResponse();
        TestController controller = new TestController();

        // When
        controller.service(request, response);

        // Then
        assertTrue(controller.doGetCalled);
        assertFalse(controller.doPostCalled);
    }

    @Test
    public void POST_요청이_들어오면_doPost_메서드를_호출한다() throws Exception {
        // Given
        String httpRequestString = "POST / HTTP/1.1\r\nHost: localhost\r\nContent-Length: 0\r\n\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(httpRequestString));
        HttpRequest request = new HttpRequest(bufferedReader);
        HttpResponse response = new HttpResponse();
        TestController controller = new TestController();

        // When
        controller.service(request, response);

        // Then
        assertTrue(controller.doPostCalled);
        assertFalse(controller.doGetCalled);
    }

    @Test
    public void 지원하지_않는_요청이_들어오면_METHOD_NOT_ALLOWED_상태코드를_설정한다() throws Exception {
        // Given
        String httpRequestString = "PUT / HTTP/1.1\r\nHost: localhost\r\n\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(httpRequestString));
        HttpRequest request = new HttpRequest(bufferedReader);
        HttpResponse response = new HttpResponse();
        TestController controller = new TestController();

        // When
        controller.service(request, response);

        // Then
        assertFalse(controller.doGetCalled);
        assertFalse(controller.doPostCalled);
        assertEquals(HttpStatusCode.METHOD_NOT_ALLOWED.getValue(), response.getStatusCode());
    }
}
