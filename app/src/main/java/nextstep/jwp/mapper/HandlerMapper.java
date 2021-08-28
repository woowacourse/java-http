package nextstep.jwp.mapper;

import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.handler.ModelAndView;

public interface HandlerMapper {
    ModelAndView mapping(RequestLine requestLine);
}
