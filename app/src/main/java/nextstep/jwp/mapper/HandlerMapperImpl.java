package nextstep.jwp.mapper;

import java.util.Arrays;
import java.util.List;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.http.request.HttpRequest;

public class HandlerMapperImpl implements HandlerMapper {

    private final List<Handler> handlers;

    private HandlerMapperImpl(List<Handler> handlers) {
        this.handlers = handlers;
    }

    public HandlerMapperImpl(Handler... handlers) {
        this(Arrays.asList(handlers));
    }

    @Override
    public Handler mapping(HttpRequest httpRequest) {
        return handlers.stream()
                .filter(handler -> handler.mapping(httpRequest))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
