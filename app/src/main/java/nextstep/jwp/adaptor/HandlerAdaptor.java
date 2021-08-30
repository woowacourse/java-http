package nextstep.jwp.adaptor;

import nextstep.jwp.handler.Handler;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface HandlerAdaptor {
    ModelAndView handle(Handler handler, HttpRequest httpRequest, HttpResponse httpResponse);
}
