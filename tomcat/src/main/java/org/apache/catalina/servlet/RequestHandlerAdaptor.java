package org.apache.catalina.servlet;

import java.util.Set;
import org.apache.catalina.servlet.handler.Servlet;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class RequestHandlerAdaptor {

    private final RequestMapping handlerMapping;

    public RequestHandlerAdaptor(final Set<Servlet> handlers) {
        handlerMapping = new RequestMapping(handlers);
    }

    public Response service(final Request request) {
        final var handler = handlerMapping.getHandler(request);

        return handler.service(request);
    }

}
