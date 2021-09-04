package nextstep.jwp.web;

import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.request.RequestLine;

import java.util.Map;
import java.util.Optional;

public class RequestMapping {

    private final Map<RequestMappingInfo, ControllerMethod> values;

    public RequestMapping(Map<RequestMappingInfo, ControllerMethod> values) {
        this.values = values;
    }

    public Optional<ControllerMethod> getControllerMethod(HttpRequest httpRequest) {
        RequestLine requestLine = httpRequest.getRequestLine();
        return Optional.ofNullable(values.get(RequestMappingInfo.of(requestLine)));
    }
}
