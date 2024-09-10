package org.apache.coyote.http11.domain.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.List;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.request.RequestBody;
import org.apache.coyote.http11.domain.request.RequestHeaders;
import org.apache.coyote.http11.domain.request.RequestLine;
import org.apache.coyote.http11.domain.response.HttpResponse;
import org.apache.coyote.http11.domain.response.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AbstractControllerTest {

    private AbstractController controller;

    @BeforeEach
    void setUp() {
        controller = new AbstractController() {
            @Override
            protected void doGet(HttpRequest request, HttpResponse response) {
                response.setStatus(HttpStatus.OK);
                response.setMessageBody("GET request handled");
            }
        };
    }

    @Test
    @DisplayName("구현한 HTTP 메서드를 처리한다.")
    void service() throws IOException {
        RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of("Host: localhost:8080", "Connection: keep-alive"));
        RequestBody requestBody = new RequestBody("test body");
        HttpRequest request = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse response = HttpResponse.status(HttpStatus.OK).build();

        controller.service(request, response);

        assertAll(
                () -> assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getMessageBody()).isEqualTo("GET request handled")
        );
    }

    @Test
    @DisplayName("구현하지 않은 HTTP 메서드를 처리한다.")
    void serviceMethodNotImplemented() throws IOException {
        RequestLine requestLine = new RequestLine("POST /index.html HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of("Host: localhost:8080", "Connection: keep-alive"));
        RequestBody requestBody = new RequestBody("test body");
        HttpRequest request = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse response = HttpResponse.status(HttpStatus.OK).build();

        controller.service(request, response);

        assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.NOT_IMPLEMENTED);
    }
}
