package nextstep.jwp.mapper;

import java.util.Arrays;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.resource.ResourceHandler;
import nextstep.jwp.http.request.HttpRequest;

import java.util.List;

public class ResourceHandlerMapper implements  HandlerMapper {

    private final List<ResourceHandler> handlers;

    public ResourceHandlerMapper(List<ResourceHandler> handlers) {
        this.handlers = handlers;
    }

    public ResourceHandlerMapper(ResourceHandler... resourceHandlers){
        this(Arrays.asList(resourceHandlers));
    }

    @Override
    public Handler mapping(HttpRequest httpRequest) {
        return handlers.stream()
                .filter(handler -> handler.mapping(httpRequest))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
