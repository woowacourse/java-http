package nextstep.jwp.handler;

import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface Handler {
    ModelAndView handle(HttpRequest httpRequest, HttpResponse httpResponse);
}
