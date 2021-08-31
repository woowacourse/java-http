package nextstep.jwp.httpserver.adapter;

import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;
import nextstep.jwp.httpserver.domain.view.ModelAndView;
import nextstep.jwp.httpserver.handler.controller.Controller;
import nextstep.jwp.httpserver.handler.controller.StaticResourceController;

public class ControllerHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof Controller) && !(handler instanceof StaticResourceController);
    }

    @Override
    public ModelAndView handle(HttpRequest httpRequest, HttpResponse httpResponse, Object handler) throws Exception {
        final Controller controller = (Controller) handler;
        final String path = getResourcePath(httpRequest.getRequestUri());
        controller.service(httpRequest, httpResponse);
        return new ModelAndView(path);
    }

    private String getResourcePath(String requestUri) {
        if (requestUri.contains("?")) {
            final int index = requestUri.indexOf("?");
            return requestUri.substring(0, index);
        }
        return requestUri;
    }
}
