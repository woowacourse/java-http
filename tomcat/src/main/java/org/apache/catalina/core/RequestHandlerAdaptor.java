package org.apache.catalina.core;

import static org.apache.catalina.core.servlet.HttpServletResponse.internalSeverError;
import static org.apache.catalina.core.servlet.HttpServletResponse.notFound;

import javassist.NotFoundException;
import org.apache.catalina.core.servlet.HttpServletRequest;
import org.apache.catalina.core.servlet.HttpServletResponse;
import org.apache.coyote.http11.response.Response;

public class RequestHandlerAdaptor {

    private final RequestMapping handlerMapping;

    public RequestHandlerAdaptor(final RequestMapping requestMapping) {
        handlerMapping = requestMapping;
    }

    public Response service(
            final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse
    ) {
        final var handler = handlerMapping.getHandler(httpServletRequest);
        try {
            handler.service(httpServletRequest, httpServletResponse);
        } catch (final NotFoundException exception) {
            httpServletResponse.set(notFound().body(exception.getMessage()));
        } catch (final Exception exception) {
            httpServletResponse.set(internalSeverError().body(exception.getMessage()));
        }

        return httpServletResponse.build();
    }

}
