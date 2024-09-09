package org.apache.coyote.mapping;

import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.RegisterHandler;
import org.apache.coyote.handler.StaticResourceHandler;
import org.apache.http.request.HttpRequest;

public class UrlHandlerMapping extends HandlerMapping {

    private static final UrlHandlerMapping INSTANCE = new UrlHandlerMapping();

    private UrlHandlerMapping() {
    }

    public static UrlHandlerMapping getInstance() {
        return INSTANCE;
    }

    @Override
    public String mapping(HttpRequest httpRequest) {
        final String uri = httpRequest.getPath();
        if (uri.contains("login")) {
            return LoginHandler.getInstance().handle(httpRequest);
        }

        if (uri.contains("register")) {
            return RegisterHandler.getInstance().handle(httpRequest);
        }

        return StaticResourceHandler.getInstance().handle(new HttpRequest("GET", "/404.html", "HTTP/1.1", null, null));
    }
}
