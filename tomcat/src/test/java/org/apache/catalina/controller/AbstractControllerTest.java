package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AbstractControllerTest {

    private final List<Handler> handlers = List.of(
            Handler.ofPost("/post", (request, response) -> response.setHttpStatus(HttpStatus.BAD_REQUEST)),
            Handler.ofGet("/get", (request, response) -> {
                response.setHttpStatus(HttpStatus.OK);
                response.addHeader("test", "hello");
            })
    );
    private final AbstractController controller = new TestAbstractController(handlers);

    @DisplayName("요청을 처리할 수 없는 컨트롤러인 경우 예외가 발생한다.")
    @Test
    void handlerNotFound() {
        HttpRequest request = new HttpRequest(
                RequestLine.of("POST /invalid HTTP/1.1 "),
                HttpHeaders.of(List.of("Content-Length: 0")),
                ""
        );

        assertThatThrownBy(() -> controller.service(request, new HttpResponse()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("요청을 처리할 수 없는 컨트롤러입니다.");
    }

    @DisplayName("올바르게 요청을 처리한다.")
    @Test
    void findHandler() {
        HttpRequest request = new HttpRequest(
                RequestLine.of("GET /get HTTP/1.1 "),
                new HttpHeaders(Map.of()),
                ""
        );
        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        HttpResponse expectedResponse = new HttpResponse(
                HttpStatus.OK,
                Map.of("test", "hello")
        );
        assertThat(response).isEqualTo(expectedResponse);
    }
}
