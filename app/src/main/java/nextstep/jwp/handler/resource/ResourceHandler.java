package nextstep.jwp.handler.resource;

import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface ResourceHandler extends Handler {

    @Override
    ModelAndView handle(HttpRequest httpRequest, HttpResponse httpResponse);

    boolean mapping(HttpRequest httpRequest);
}
