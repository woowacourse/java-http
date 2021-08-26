package nextstep.jwp.http;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.http.mapper.ControllerMapper;
import nextstep.jwp.http.message.request.HttpRequestMessage;
import nextstep.jwp.http.message.request.RequestHeader;
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
        RequestHeader requestHeader = httpRequestMessage.getHeader();
        String requestUri = requestHeader.uri();
        Controller controller = controllerMapper.matchController(requestUri);
        controller.service(httpRequestMessage, httpResponseMessage);
        return httpResponseMessage;
    }
}
