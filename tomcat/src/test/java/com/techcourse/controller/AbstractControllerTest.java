package com.techcourse.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    @Test
    void getOnlyControllerRejectsPostAndAdvertisesOnlyGet() throws Exception {
        var controller = new AbstractController() {
            @Override
            protected List<String> allowedMethods() {
                return List.of("GET");
            }

            @Override
            protected void doGet(HttpRequest request, HttpResponse response) {
                response.copyFrom(HttpResponse.ok("text/plain", "hello".getBytes(StandardCharsets.UTF_8)));
            }
        };
        var response = new HttpResponse();
        controller.service(request("POST"), response);
        assertThat(new String(response.toBytes(), StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 405 Method Not Allowed\r\n")
                .contains("\r\nAllow: GET\r\n");

        var getResponse = new HttpResponse();
        controller.service(request("GET"), getResponse);
        assertThat(new String(getResponse.toBytes(), StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 200 OK\r\n").endsWith("hello");
    }

    @Test
    void defaultHandlersReturn405InsteadOfEmptySuccess() throws Exception {
        var controller = new AbstractController() {
            @Override
            protected List<String> allowedMethods() {
                return List.of();
            }
        };
        for (String method : List.of("GET", "POST")) {
            var response = new HttpResponse();
            if (method.equals("GET")) {
                controller.doGet(request(method), response);
            } else {
                controller.doPost(request(method), response);
            }
            assertThat(new String(response.toBytes(), StandardCharsets.UTF_8))
                    .startsWith("HTTP/1.1 405 Method Not Allowed\r\n")
                    .contains("\r\nAllow: \r\n");
        }
    }

    private HttpRequest request(String method) {
        return HttpRequest.parse(method + " / HTTP/1.1", Map.of(), new byte[0]);
    }
}
