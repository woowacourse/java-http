package nextstep.jwp.http;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.http.mapper.ControllerMapper;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.response.HttpResponseMessage;

import java.io.IOException;

public class HttpResponseBuilder {

    private final HttpRequestMessage httpRequestMessage;
    private final ControllerMapper controllerMapper;

    public HttpResponseBuilder(HttpRequestMessage httpRequestMessage) {
        this.httpRequestMessage = httpRequestMessage;
        this.controllerMapper = new ControllerMapper();
    }

    public HttpResponseMessage build() throws IOException {
        HttpResponseMessage httpResponseMessage = new HttpResponseMessage();
        Controller controller = findNonForwardController(httpResponseMessage);
        controller.service(httpRequestMessage, httpResponseMessage);
        return httpResponseMessage;
    }

    private Controller findNonForwardController(HttpResponseMessage httpResponseMessage) throws IOException {
        Controller controller = controllerMapper.matchController(httpRequestMessage);
        if (controller.canForward()) {
            controller.service(httpRequestMessage, httpResponseMessage);
            controller = findNonForwardController(httpResponseMessage);
        }
        return controller;
    }
}
