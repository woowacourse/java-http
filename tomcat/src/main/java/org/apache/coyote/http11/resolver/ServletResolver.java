package org.apache.coyote.http11.resolver;

import org.apache.coyote.http11.data.Request;
import org.apache.coyote.http11.data.Response;
import org.apache.coyote.http11.filter.FilterChainFactory;
import org.apache.coyote.http11.handler.RequestHandler;

public class ServletResolver implements RequestResolver {
    private final RequestHandler[] handlers;
    private final FilterChainFactory filterChainFactory;
    private final ViewResolver viewResolver;

    public static ServletResolver create(
            FilterChainFactory filterChainFactory,
            ViewResolver viewResolver,
            RequestHandler... handlers) {
        return new ServletResolver(filterChainFactory, viewResolver, handlers);
    }

    private ServletResolver(
            FilterChainFactory filterChainFactory,
            ViewResolver viewResolver,
            RequestHandler... handlers) {
        this.handlers = handlers;
        this.filterChainFactory = filterChainFactory;
        this.viewResolver = viewResolver;
    }

    @Override
    public Response handleRequest(Request request) {
        final Response response = filterChainFactory.create(this::handleServlet).doFilter(request);
        return viewResolver.resolve(response);
    }

    private Response handleServlet(Request request) {
        for (RequestHandler servlet : handlers) {
            if (servlet.canHandle(request)) {
                return switch (request.getRequestPoint().getMethod()) {
                    case "GET" -> servlet.doGet(request);
                    case "POST" -> servlet.doPost(request);
                    default -> Response.badRequest();
                };
            }
        }

        return Response.notFound();
    }

    @Override
    public boolean canHandle(Request request) {
        return true;
    }
}
