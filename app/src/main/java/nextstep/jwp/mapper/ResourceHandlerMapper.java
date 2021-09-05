package nextstep.jwp.mapper;

import java.util.List;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.resource.ResourceHandlerImpl;
import nextstep.jwp.http.request.HttpRequest;

public class ResourceHandlerMapper implements HandlerMapper {

    private final List<ResourceHandlerImpl> handlers;

    public ResourceHandlerMapper(List<ResourceHandlerImpl> handlers) {
        this.handlers = handlers;
    }

    @Override
    public Handler mapping(HttpRequest httpRequest) {
        return handlers.stream()
                .filter(handler -> handler.mapping(httpRequest))
                .findAny()
                .orElse(null);
    }
}
