package org.apache.catalina.servlet;

import java.util.List;
import org.apache.catalina.servlet.handler.DefaultHandler;
import org.apache.catalina.servlet.handler.LoginHandler;
import org.apache.catalina.servlet.handler.RegisterHandler;
import org.apache.catalina.servlet.handler.RequestHandler;
import org.apache.coyote.http11.request.Request;

public class RequestHandlerMapping {

    private static final List<RequestHandler> handlers;
    private static final RequestHandler defaultHandler;

    static {
        handlers = List.of(new LoginHandler(), new RegisterHandler());
        defaultHandler = new DefaultHandler();
    }

    private RequestHandlerMapping() {
    }

    public static RequestHandler getHandler(final Request request) {
        return handlers.stream()
                .filter(requestHandler -> requestHandler.canHandle(request))
                .findFirst()
                .orElse(defaultHandler);
    }

}
