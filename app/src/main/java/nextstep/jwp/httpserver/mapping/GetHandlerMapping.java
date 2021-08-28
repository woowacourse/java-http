package nextstep.jwp.httpserver.mapping;

import nextstep.jwp.httpserver.BeanFactory;
import nextstep.jwp.httpserver.controller.Controller;
import nextstep.jwp.httpserver.domain.HttpMethod;
import nextstep.jwp.httpserver.domain.request.HttpRequest;

public class GetHandlerMapping implements HandlerMapping {

    @Override
    public boolean isHandle(HttpRequest httpRequest) {
        final String requestUri = httpRequest.getRequestUri();
        return (HttpMethod.GET == httpRequest.getHttpMethod())
                && !requestUri.endsWith(".html")
                && !requestUri.equals("/");
    }

    @Override
    public Controller find(HttpRequest httpRequest) {
        String requestUri = httpRequest.getRequestUri();

        if (requestUri.contains("?")) {
            final int index = requestUri.indexOf("?");
            requestUri = requestUri.substring(0, index);
        }

        return (Controller) BeanFactory.getHandler(requestUri);
    }
}
