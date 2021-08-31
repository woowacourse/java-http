package nextstep.jwp.httpserver.adapter;

import nextstep.jwp.httpserver.domain.request.HttpRequest;
import nextstep.jwp.httpserver.domain.response.HttpResponse;
import nextstep.jwp.httpserver.domain.view.ModelAndView;
import nextstep.jwp.httpserver.handler.controller.StaticResourceController;

public class StaticResourceHandlerAdapter implements HandlerAdapter {

    @Override
    public boolean supports(Object handler) {
        return (handler instanceof StaticResourceController);
    }

    @Override
    public ModelAndView handle(HttpRequest httpRequest, HttpResponse httpResponse, Object handler) throws Exception {
        final StaticResourceController staticResourceController = (StaticResourceController) handler;
        final String path = getResourcePath(httpRequest.getRequestUri());
        staticResourceController.service(httpRequest, httpResponse);
        return new ModelAndView(path);
    }

    private String getResourcePath(String requestUri) {
        if (requestUri.equals("/")) {
            return "/index.html";
        }
        return requestUri;
    }
}
