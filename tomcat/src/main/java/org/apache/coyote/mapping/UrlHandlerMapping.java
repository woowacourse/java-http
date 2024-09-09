package org.apache.coyote.mapping;

import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.RegisterHandler;
import org.apache.http.request.HttpRequest;

public class UrlHandlerMapping extends HandlerMapping {

    private static final UrlHandlerMapping INSTANCE = new UrlHandlerMapping();

    private UrlHandlerMapping() {
    }

    @Override
    public String mapping(HttpRequest httpRequest) {
        final String uri = httpRequest.getUrl();
        if (uri.contains("login")) {
            return LoginHandler.getInstance().handle(httpRequest);
        }

        if (uri.contains("register")) {
            return RegisterHandler.getInstance().handle(httpRequest);
        }

        return ResourceHandlerMapping.getInstance().handleSimpleResource("404.html");
    }

    public static UrlHandlerMapping getInstance() {
        return INSTANCE;
    }
}
