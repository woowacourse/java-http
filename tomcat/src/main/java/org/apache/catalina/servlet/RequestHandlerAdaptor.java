package org.apache.catalina.servlet;

import static org.apache.coyote.http11.response.Response.internalSeverError;
import static org.apache.coyote.http11.response.Response.notFound;

import java.util.Set;
import javassist.NotFoundException;
import org.apache.catalina.servlet.handler.Servlet;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.response.Response.ServletResponse;

public class RequestHandlerAdaptor {

    private final RequestMapping handlerMapping;

    public RequestHandlerAdaptor(final Set<Servlet> handlers) {
        handlerMapping = new RequestMapping(handlers);
    }

    public Response service(final Request request, final ServletResponse response) {
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
