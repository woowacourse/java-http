package nextstep.jwp.mapper;

import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.handler.ModelAndView;

public interface HandlerMapper {
    boolean mapping(RequestLine requestLine);

    ModelAndView service(RequestLine requestLine);
}
