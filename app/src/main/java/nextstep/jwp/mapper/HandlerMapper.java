package nextstep.jwp.mapper;

import nextstep.jwp.handler.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;

public interface HandlerMapper {
    boolean mapping(RequestLine requestLine);

    ModelAndView service(HttpRequest httpRequest);
}
