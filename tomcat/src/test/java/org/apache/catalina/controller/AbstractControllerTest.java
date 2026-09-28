package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    private final Controller controller = new AbstractController() {
        @Override
        protected void doGet(final HttpRequest request, final HttpResponse response) {
            response.body("GET response");
        }

        @Override
        protected void doPost(final HttpRequest request, final HttpResponse response) {
            response.body("POST response");
        }
    };

    @Test
    void dispatchesGet() throws Exception {
        assertThat(service(controller, "GET")).endsWith("\r\n\r\nGET response");
    }

    @Test
    void dispatchesPost() throws Exception {
        assertThat(service(controller, "POST")).endsWith("\r\n\r\nPOST response");
    }

    @Test
    void doesNothingForOtherMethods() throws Exception {
        assertThat(service(controller, "DELETE")).endsWith("Content-Length: 0\r\n\r\n");
    }

    @Test
    void defaultHandlersDoNothing() throws Exception {
        final Controller controller = new AbstractController() { };

        assertThat(service(controller, "GET")).endsWith("Content-Length: 0\r\n\r\n");
        assertThat(service(controller, "POST")).endsWith("Content-Length: 0\r\n\r\n");
    }

    private String service(final Controller controller, final String method) throws Exception {
        final var request = HttpRequest.read(new ByteArrayInputStream(
                (method + " / HTTP/1.1\r\n\r\n").getBytes(StandardCharsets.UTF_8)));
        final var output = new ByteArrayOutputStream();
        final var response = new HttpResponse(output);
        controller.service(request, response);
        response.flush();
        return output.toString(StandardCharsets.UTF_8);
    }
}
