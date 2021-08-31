package nextstep.jwp.mapper;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.http.request.HttpRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HandlerMapperImpl implements HandlerMapper {

    private final List<HandlerMapper> handlerMappers;

    private HandlerMapperImpl(List<HandlerMapper> handlerMappers) {
        this.handlerMappers = handlerMappers;
    }

    public HandlerMapperImpl(HandlerMapper... handlerMappers) {
        this(Arrays.asList(handlerMappers));
    }

    @Override
    public Handler mapping(HttpRequest httpRequest) {
        return handlerMappers.stream()
                .map(handler -> handler.mapping(httpRequest))
                .filter(handler->!Objects.isNull(handler))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
