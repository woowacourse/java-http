package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import org.apache.catalina.exception.TomcatException;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    private final AbstractController postOKController = new TestAbstractController(Handler.ofPost("/post",
            (request, response) -> response.setHttpStatus(HttpStatus.OK)));
    private final AbstractController postFoundController = new TestAbstractController(Handler.ofPost("/post",
            ((request, response) -> response.setHttpStatus(HttpStatus.FOUND))));
    private final AbstractController getOKController = new TestAbstractController(Handler.ofGet("/get",
            ((request, response) -> response.setHttpStatus(HttpStatus.OK))));

    @DisplayName("중복된 Endpoint를 등록하는 경우 예외가 발생한다.")
    @Test
    void endpointDuplicated() {
        assertThatThrownBy(() -> RequestMapping.of(postFoundController, postOKController))
                .isInstanceOf(TomcatException.class)
                .hasMessage("HttpEndpoint 중복으로 인해 컨트롤러 등록에 실패하였습니다.");
    }

    @DisplayName("요청을 처리할 수 있는 컨트롤러를 반환한다.")
    @Test
    void getController() {
        RequestMapping requestMapping = RequestMapping.of(postOKController, getOKController);
        HttpRequest request = new HttpRequest(
                RequestLine.of("GET /get HTTP/1.1 "),
                new HttpHeaders(Map.of()),
                ""
        );

        Controller controller = requestMapping.getController(request);

        assertThat(controller).isEqualTo(getOKController);
    }

    @DisplayName("요청을 처리할 수 있는 컨트롤러가 없는 경우 ResourceCotnroller를 반환한다.")
    @Test
    void getResourceController() {
        RequestMapping requestMapping = RequestMapping.of(postOKController);
        HttpRequest request = new HttpRequest(
                RequestLine.of("GET /hello HTTP/1.1 "),
                new HttpHeaders(Map.of()),
                ""
        );

        Controller controller = requestMapping.getController(request);

        assertThat(controller).isInstanceOf(ResourceController.class);
    }
}
