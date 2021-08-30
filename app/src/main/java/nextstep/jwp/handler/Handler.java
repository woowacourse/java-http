package nextstep.jwp.handler;

import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface Handler {
    boolean mapping(HttpRequest httpRequest);

    ModelAndView service(HttpRequest httpRequest, HttpResponse httpResponse);
}
