package org.apache.catalina.core;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.catalina.core.servlet.DefaultServlet;
import org.apache.catalina.core.servlet.HttpServlet;
import org.apache.coyote.http11.request.Request;

public class RequestMapping {

    private final Map<String, HttpServlet> servlets;
    private final RequestHandler defaultHandler = new DefaultServlet();

    public RequestMapping(final Set<HttpServlet> httpServlets) {
        this.servlets = httpServlets.stream()
                .collect(Collectors.toMap(
                        HttpServlet::getMappingPath,
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
