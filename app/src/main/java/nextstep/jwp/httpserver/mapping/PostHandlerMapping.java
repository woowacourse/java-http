package nextstep.jwp.httpserver.mapping;

import nextstep.jwp.httpserver.BeanFactory;
import nextstep.jwp.httpserver.controller.Controller;
import nextstep.jwp.httpserver.domain.HttpMethod;
import nextstep.jwp.httpserver.domain.request.HttpRequest;

public class PostHandlerMapping implements HandlerMapping {

    @Override
    public boolean isHandle(HttpRequest httpRequest) {
        return (HttpMethod.POST == httpRequest.getHttpMethod());
    }

    @Override
    public Controller find(HttpRequest httpRequest) {
        final String requestUri = httpRequest.getRequestUri();
        return (Controller) BeanFactory.getHandler(requestUri);
    }
}
