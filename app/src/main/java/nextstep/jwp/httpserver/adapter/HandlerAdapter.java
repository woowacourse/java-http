package nextstep.jwp.httpserver.adapter;

import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;
import nextstep.jwp.httpserver.domain.view.ModelAndView;

public interface HandlerAdapter {
    boolean supports(Object handler);

    ModelAndView handle(HttpRequest httpRequest, HttpResponse httpResponse, Object handler) throws Exception;
}
