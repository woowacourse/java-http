package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    @Test
    void dispatchesGet() throws Exception {
        assertDispatch("GET", "GET");
    }

    @Test
    void dispatchesPost() throws Exception {
        assertDispatch("POST", "POST");
    }

    @Test
    void dispatchesOtherMethodsToGet() throws Exception {
        for (String method : List.of("PUT", "HEAD", "DELETE")) {
            assertDispatch(method, "GET");
        }
    }

    @Test
    void propagatesHandlerException() throws IOException {
        Exception failure = new Exception("handler failed");
        Controller controller = new AbstractController() {
            @Override
            protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
                throw failure;
            }
        };
        HttpRequest request = request("POST");

        assertThatThrownBy(() -> controller.service(request, new HttpResponse()))
                .isSameAs(failure);
    }

    @Test
    void defaultHandlersLeaveResponseUntouched() throws Exception {
        Controller controller = new AbstractController() {
        };
        for (String method : List.of("GET", "POST")) {
            HttpResponse response = new HttpResponse();
            response.redirect("/existing");

            controller.service(request(method), response);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            response.writeTo(output);
            assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(
                    "HTTP/1.1 302 Found\r\nLocation: /existing\r\nContent-Length: 0\r\n\r\n");
        }
    }

    private void assertDispatch(String method, String expectedHandler) throws Exception {
        RecordingController controller = new RecordingController();
        HttpRequest request = request(method);
        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        assertThat(controller.calls).containsExactly(expectedHandler);
        assertThat(controller.receivedRequest).isSameAs(request);
        assertThat(controller.receivedResponse).isSameAs(response);
    }

    private HttpRequest request(String method) throws IOException {
        String message = method + " /login HTTP/1.1\r\n\r\n";
        return HttpRequest.readFrom(new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8)));
    }

    private static class RecordingController extends AbstractController {

        private final List<String> calls = new ArrayList<>();
        private HttpRequest receivedRequest;
        private HttpResponse receivedResponse;

        @Override
        protected void doGet(HttpRequest request, HttpResponse response) {
            record("GET", request, response);
        }

        @Override
        protected void doPost(HttpRequest request, HttpResponse response) {
            record("POST", request, response);
        }

        private void record(String handler, HttpRequest request, HttpResponse response) {
            calls.add(handler);
            receivedRequest = request;
            receivedResponse = response;
        }
    }
}
