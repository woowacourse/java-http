package nextstep.jwp.mapper;

import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.view.View;

public interface HandlerMapper {
    View mapping(RequestLine requestLine);
}
