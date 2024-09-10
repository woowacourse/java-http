package org.apache.coyote.http11.domain.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.domain.request.HttpRequest;
import org.apache.coyote.http11.domain.request.RequestBody;
import org.apache.coyote.http11.domain.request.RequestHeaders;
import org.apache.coyote.http11.domain.request.RequestLine;
import org.apache.coyote.http11.domain.response.HttpResponse;
import org.apache.coyote.http11.domain.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    @DisplayName("요청을 처리할 수 있는 컨트롤러를 가져온다.")
    void getController() throws IOException {
        RequestMapping requestMapping = new RequestMapping(Map.of(
                "/test", new AbstractController() {
                    @Override
                    protected void doGet(HttpRequest request, HttpResponse response) {
                        response.setStatus(HttpStatus.OK);
                        response.setMessageBody("test body");
                    }
                }
        ));
        RequestLine requestLine = new RequestLine("GET /test HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of("Host: localhost:8080", "Connection: keep-alive"));
        RequestBody requestBody = new RequestBody("test body");
        HttpRequest request = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse response = HttpResponse.status(HttpStatus.OK).build();

        requestMapping.getController(request).service(request, response);

        assertAll(
                () -> assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getMessageBody()).isEqualTo("test body")
        );
    }

    @Test
    @DisplayName("요청을 처리할 수 있는 컨트롤러가 없으면 ResourceController 를 반환한다.")
    void getControllerNoMatchedController() throws IOException {
        RequestMapping requestMapping = new RequestMapping(Map.of());
        RequestLine requestLine = new RequestLine("GET /not/matched HTTP/1.1");
        RequestHeaders requestHeaders = new RequestHeaders(List.of("Host: localhost:8080", "Connection: keep-alive"));
        RequestBody requestBody = new RequestBody("test body");
        HttpRequest request = new HttpRequest(requestLine, requestHeaders, requestBody);

        Controller controller = requestMapping.getController(request);

        assertThat(controller).isInstanceOf(ResourceController.class);
    }
}
