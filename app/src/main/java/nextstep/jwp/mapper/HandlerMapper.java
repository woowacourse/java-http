package nextstep.jwp.mapper;

import nextstep.jwp.handler.Handler;
import nextstep.jwp.http.request.RequestLine;

public interface HandlerMapper {
    Handler findHandler(RequestLine requestLine);
}
