package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.header.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    static class TestAbstractController extends AbstractController {

        boolean postCalled;
        boolean getCalled;

        @Override
        void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
            postCalled = true;
        }

        @Override
        void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
            getCalled = true;
        }

        @Override
        public boolean canHandle(String url) {
            return false;
        }
    }

    private Controller testController;

    @DisplayName("post 요청의 경우 doPost가 실행된다")
    @Test
    void callPost(){
        TestAbstractController controller = new TestAbstractController();
        testController = controller;

        HttpRequest httpRequest = new HttpRequest(new RequestLine("POST /register HTTP/1.1"), new HttpRequestHeader());
        testController.service(httpRequest, new HttpResponse());

        assertThat(controller.postCalled).isTrue();
    }

    @DisplayName("get 요청의 경우 doGet이 실행된다")
    @Test
    void callGet(){
        TestAbstractController controller = new TestAbstractController();
        testController = controller;

        HttpRequest httpRequest = new HttpRequest(new RequestLine("GET /login.html HTTP/1.1"), new HttpRequestHeader());
        testController.service(httpRequest, new HttpResponse());

        assertThat(controller.getCalled).isTrue();
    }
}
