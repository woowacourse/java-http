package nextstep.jwp.httpserver.mapping;

import nextstep.jwp.httpserver.BeanFactory;
import nextstep.jwp.httpserver.domain.request.HttpRequest;

public class StaticResourceHandlerMapping implements HandlerMapping {

    @Override
    public boolean canUse(HttpRequest httpRequest) {
        final String requestUri = httpRequest.getRequestUri();
        return requestUri.endsWith(".html") || requestUri.endsWith(".css") || requestUri.endsWith(".js");
    }

    @Override
    public Object getHandler(HttpRequest httpRequest) {
        return BeanFactory.getBean("staticResourceController");
    }
}
