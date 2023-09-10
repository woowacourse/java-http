package org.apache.catalina.servlet;

import java.util.Set;
import org.apache.catalina.servlet.handler.RequestHandler;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class DispatcherServlet {

    private final RequestHandlerMapping handlerMapping;

    public DispatcherServlet(final Set<RequestHandler> handlers) {
        handlerMapping = new RequestHandlerMapping(handlers);
    }

    public Response service(final Request request) {
        final var handler = handlerMapping.getHandler(request);

        return handler.service(request);
    }

}
