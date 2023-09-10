package org.apache.catalina.core;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.catalina.core.servlet.HttpServlet;
import org.apache.coyote.http11.request.Request;

public class RequestMapping implements RequestMapping {

    private final Map<String, HttpServlet> servlets;
    private final RequestHandler defaultHandler;

    public RequestMapping(final Set<HttpServlet> httpServlets, final RequestHandler defaultHandler) {
        this.servlets = httpServlets.stream()
                .collect(Collectors.toMap(
                        HttpServlet::getMappingPath,
                        handler -> handler
                ));
        this.defaultHandler = defaultHandler;
    }

    public RequestHandler getHandler(final Request request) {
        final var httpServlet = servlets.get(request.getPath());
        if (httpServlet == null) {
            return defaultHandler;
        }

        return httpServlet;
    }

}
