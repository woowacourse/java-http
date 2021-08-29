package nextstep.jwp.mapper;

import java.util.Arrays;
import java.util.List;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.handler.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.request.RequestUriPath;

public class HandlerMappers {
    private final List<HandlerMapper> handlerMappers;

    public HandlerMappers() {
        handlerMappers = Arrays.asList(
                new ControllerMapper(),
                new ResourceMapper()
        );
    }

    public ModelAndView handle(HttpRequest httpRequest) {
        HandlerMapper handlerMapper = searchHandlerMapper(httpRequest.getRequestLine());
        return handlerMapper.service(httpRequest);
    }

    private HandlerMapper searchHandlerMapper(RequestLine requestLine) {
        return handlerMappers.stream()
                .filter(handlerMapper -> handlerMapper.mapping(requestLine))
                .findFirst()
                .orElseThrow(BadRequestException::new);
    }
}
