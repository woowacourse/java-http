package nextstep.jwp.handler;

import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;

public interface Handler {
    boolean mapping(RequestLine requestLine);

    ModelAndView service(HttpRequest httpRequest);
}
