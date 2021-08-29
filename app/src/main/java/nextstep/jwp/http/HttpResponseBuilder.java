package nextstep.jwp.http;

import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.controller.ControllerAdvice;
import nextstep.jwp.http.common.HttpPath;
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
        try {
            Controller finalController = findFinalController(httpResponseMessage);
            finalController.service(httpRequestMessage, httpResponseMessage);
        } catch (RuntimeException e) {
            ControllerAdvice.getInstance().run(httpResponseMessage, e);
        }
        return httpResponseMessage;
    }

    private Controller findFinalController(HttpResponseMessage httpResponseMessage) throws IOException {
        HttpPath beforePath = httpRequestMessage.requestPath();
        Controller controller = controllerMapper.matchController(httpRequestMessage);
        controller.service(httpRequestMessage, httpResponseMessage);
        HttpPath afterPath = httpRequestMessage.requestPath();

        if (!beforePath.equals(afterPath)) {
            return findFinalController(httpResponseMessage);
        }
        return controller;
    }
}
