package nextstep.jwp.mapper;

import java.util.List;
import java.util.Objects;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.handler.Handler;
import nextstep.jwp.http.request.HttpRequest;

public class HandlerMappers {

    private final List<HandlerMapper> mappers;

    public HandlerMappers(List<HandlerMapper> mappers) {
        this.mappers = mappers;
    }

    public Handler mapping(HttpRequest httpRequest) {
        return mappers.stream()
                .map(mapper -> mapper.mapping(httpRequest))
                .filter(handler -> !Objects.isNull(handler))
                .findAny()
                .orElseThrow(NotFoundException::new);
    }
}
