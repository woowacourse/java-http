package org.apache.catalina.servlet;

import static org.apache.catalina.servlet.handler.ServletResponse.internalSeverError;
import static org.apache.catalina.servlet.handler.ServletResponse.notFound;

import java.util.Set;
import javassist.NotFoundException;
import org.apache.catalina.servlet.handler.Servlet;
import org.apache.catalina.servlet.handler.ServletResponse;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

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
