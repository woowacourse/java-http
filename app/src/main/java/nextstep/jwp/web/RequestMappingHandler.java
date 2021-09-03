package nextstep.jwp.web;

import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.RequestLine;

import java.util.Map;
import java.util.Optional;

public class RequestMappingHandler {

    private final Map<RequestMapping, ControllerMethod> requestMapping;

    public RequestMappingHandler(Map<RequestMapping, ControllerMethod> requestMapping) {
        this.requestMapping = requestMapping;
    }

    public Optional<ControllerMethod> getControllerMethod(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();
        return Optional.ofNullable(requestMapping.get(RequestMapping.of(requestLine)));
    }
}
