package org.apache.coyote.http11;

import java.util.List;
import org.apache.coyote.http11.handler.DefaultHandler;
import org.apache.coyote.http11.handler.LoginHandler;
import org.apache.coyote.http11.handler.RegisterHandler;
import org.apache.coyote.http11.handler.RequestHandler;
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
