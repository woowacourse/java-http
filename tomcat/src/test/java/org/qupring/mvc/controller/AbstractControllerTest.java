package org.qupring.mvc.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.http.HttpMethod;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.HttpTomcatRequest;
import org.apache.http.response.HttpResponse;
import org.apache.http.response.HttpTomcatResponse;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    @Test
    void GET_요청을_doGet으로_위임한다() throws Exception {
        // given
        TestController controller = new TestController();
        HttpResponse response = HttpTomcatResponse.createDefault();

        // when
        controller.service(request(HttpMethod.GET), response);

        // then
        assertThat(response.getBody()).isEqualTo("GET");
    }

    @Test
    void POST_요청을_doPost로_위임한다() throws Exception {
        // given
        TestController controller = new TestController();
        HttpResponse response = HttpTomcatResponse.createDefault();

        // when
        controller.service(request(HttpMethod.POST), response);

        // then
        assertThat(response.getBody()).isEqualTo("POST");
    }

    private HttpRequest request(HttpMethod method) {
        return new HttpTomcatRequest(
                method,
                "/test",
                "HTTP/1.1",
                null,
                Map.of(),
                Map.of(),
                Map.of(),
                Map.of()
        );
    }

    private static class TestController extends AbstractController {

        @Override
        protected void doGet(HttpRequest request, HttpResponse response) {
            response.setBody("GET");
        }

        @Override
        protected void doPost(HttpRequest request, HttpResponse response) {
            response.setBody("POST");
        }
    }
}
