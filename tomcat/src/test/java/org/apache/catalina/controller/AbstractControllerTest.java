package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    @Test
    void GET_요청은_get으로_전달한다() throws IOException {
        TestController controller = new TestController();
        HttpRequest request = new HttpRequest(new BufferedReader(new StringReader(
                "GET /login HTTP/1.1\r\n\r\n")));

        controller.service(request, new HttpResponse());

        assertThat(controller.calledMethod).isEqualTo("GET");
    }

    @Test
    void POST_요청은_post로_전달한다() throws IOException {
        TestController controller = new TestController();
        HttpRequest request = new HttpRequest(new BufferedReader(new StringReader(
                "POST /login HTTP/1.1\r\nContent-Length: 0\r\n\r\n")));

        controller.service(request, new HttpResponse());

        assertThat(controller.calledMethod).isEqualTo("POST");
    }

    private static class TestController extends AbstractController {

        private String calledMethod;

        @Override
        protected void get(HttpRequest request, HttpResponse response) {
            calledMethod = "GET";
        }

        @Override
        protected void post(HttpRequest request, HttpResponse response) {
            calledMethod = "POST";
        }
    }
}
