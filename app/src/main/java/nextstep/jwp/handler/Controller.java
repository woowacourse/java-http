package nextstep.jwp.handler;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;

public interface Controller {
    boolean mapping(RequestLine requestLine);

    ModelAndView service(HttpRequest httpRequest);
}
