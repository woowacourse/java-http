package nextstep.jwp.adapter;

import nextstep.jwp.model.httpmessage.request.HttpRequest;
import nextstep.jwp.model.httpmessage.response.HttpResponse;
import nextstep.jwp.view.ModelAndView;

import java.io.IOException;

public interface HandlerAdapter {

    boolean supports(Object handler);

    ModelAndView handle(HttpRequest request, HttpResponse response, Object handler) throws IOException;
}
