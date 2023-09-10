package org.apache.catalina.core;

import static org.apache.catalina.core.servlet.HttpServletResponse.internalSeverError;
import static org.apache.catalina.core.servlet.HttpServletResponse.notFound;

import java.util.Set;
import javassist.NotFoundException;
import org.apache.catalina.core.servlet.HttpServlet;
import org.apache.catalina.core.servlet.HttpServletResponse;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class RequestHandlerAdaptor {

    private final RequestMapping handlerMapping;

    public RequestHandlerAdaptor(final Set<HttpServlet> handlers) {
        handlerMapping = new RequestMapping(handlers);
    }

    public Response service(final Request request, final HttpServletResponse response) {
        final var handler = handlerMapping.getHandler(request);
        try {
            handler.service(request, response);
        } catch (final NotFoundException exception) {
            response.set(notFound().body(exception.getMessage()));
        } catch (final Exception exception) {
            response.set(internalSeverError().body(exception.getMessage()));
        }

        return response.build();
    }

}
