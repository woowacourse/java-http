package nextstep.jwp.mapper;

import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.http.request.HttpRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class HandlerMappers implements HandlerMapper {

    private final List<HandlerMapper> mappers;

    private HandlerMappers(List<HandlerMapper> mappers) {
        this.mappers = mappers;
    }

    public HandlerMappers(HandlerMapper... mappers) {
        this(Arrays.asList(mappers));
    }

    @Override
    public Handler mapping(HttpRequest httpRequest) {
        return mappers.stream()
                .map(handler -> handler.mapping(httpRequest))
                .filter(handler->!Objects.isNull(handler))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
