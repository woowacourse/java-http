package org.apache.catalina.core;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.catalina.core.servlet.Servlet;
import org.apache.catalina.core.servlet.StaticResourceRequestHandler;
import org.apache.coyote.http11.request.Request;

public class RequestMapping {

    private final Map<String, Servlet> servlets;
    private final RequestHandler defaultHandler = new StaticResourceRequestHandler();

    public RequestMapping(final Set<Servlet> servlets) {
        this.servlets = servlets.stream()
                .collect(Collectors.toMap(
                        Servlet::getMappingPath,
                        handler -> handler
                ));
    }

    public RequestHandler getHandler(final Request request) {
        final var httpServlet = servlets.get(request.getPath());
        if (httpServlet == null) {
            return defaultHandler;
        }
        return httpServlet;
    }

}
