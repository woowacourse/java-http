package org.apache.coyote.http11.resolver;

import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;
import org.apache.coyote.http11.filter.FilterChainFactory;
import org.apache.coyote.http11.handler.RequestHandler;

public class ServletResolver implements RequestResolver {
    private final RequestHandler[] handlers;
    private final FilterChainFactory filterChainFactory;

    public static ServletResolver create(FilterChainFactory filterChainFactory, RequestHandler... handlers) {
        return new ServletResolver(filterChainFactory, handlers);
    }

    private ServletResolver(
            FilterChainFactory filterChainFactory,
            RequestHandler... handlers) {
        this.handlers = handlers;
        this.filterChainFactory = filterChainFactory;
    }

    @Override
    public Response handleRequest(Request request) {
        return filterChainFactory.create(this::handleServlet).doFilter(request);
    }

    private Response handleServlet(Request request) {
        for (RequestHandler servlet : handlers) {
            if (servlet.canHandle(request)) {
                return servlet.handle(request);
            }
        }

        return Response.notFound();
    }

    @Override
    public boolean canHandle(Request request) {
        return true;
    }
}
