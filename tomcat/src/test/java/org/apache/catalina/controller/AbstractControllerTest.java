package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    @Test
    void doGet() throws NoSuchMethodException {
        // given
        AbstractController abstractController = new TestController();
        HttpRequest request = new HttpRequest(List.of("GET / HTTP/1.1"), "");
        HttpResponse response = new HttpResponse();

        // when
        abstractController.service(request, response);

        // then
        assertThat(response.getReponse()).contains("Content-Length: 1");
    }

    @Test
    void doPost() throws NoSuchMethodException {
        // given
        AbstractController abstractController = new TestController();
        HttpRequest request = new HttpRequest(List.of("POST / HTTP/1.1"), "");
        HttpResponse response = new HttpResponse();

        // when
        abstractController.service(request, response);

        // then
        assertThat(response.getReponse()).contains("Content-Length: 0");
    }
}
