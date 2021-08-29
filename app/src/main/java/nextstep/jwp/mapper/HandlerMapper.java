package nextstep.jwp.mapper;

import nextstep.jwp.handler.Handler;
import nextstep.jwp.http.request.HttpRequest;

public interface HandlerMapper {
    Handler mapping(HttpRequest httpRequest);
}
