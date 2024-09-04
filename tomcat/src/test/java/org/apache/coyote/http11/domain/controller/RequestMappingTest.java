package org.apache.coyote.http11.domain.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.domain.request.HttpRequest;
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
                    protected HttpResponse doGet(HttpRequest request) {
                        return HttpResponse.status(HttpStatus.OK).body("test body").build();
                    }
                }
        ));
        String requestLine = "GET /test HTTP/1.1";
        List<String> headerLines = List.of("Host: localhost:8080", "Connection: keep-alive");
        HttpRequest request = new HttpRequest(requestLine, headerLines);

        HttpResponse response = requestMapping.getController(request).service(request);

        assertAll(
                () -> assertThat(response.getHttpStatus()).isEqualTo(HttpStatus.OK),
                () -> assertThat(response.getMessageBody()).isEqualTo("test body")
        );
    }

    @Test
    @DisplayName("요청을 처리할 수 있는 컨트롤러가 없으면 ResourceController 를 반환한다.")
    void getControllerNoMatchedController() throws IOException {
        RequestMapping requestMapping = new RequestMapping(Map.of());
        String requestLine = "GET /not/matched HTTP/1.1";
        List<String> headerLines = List.of("Host: localhost:8080", "Connection: keep-alive");
        HttpRequest request = new HttpRequest(requestLine, headerLines);

        Controller controller = requestMapping.getController(request);

        assertThat(controller).isInstanceOf(ResourceController.class);
    }
}
