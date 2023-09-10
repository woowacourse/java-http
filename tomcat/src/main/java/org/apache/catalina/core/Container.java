package org.apache.catalina.core;

import java.util.Set;
import org.apache.catalina.core.servlet.DefaultServlet;
import org.apache.catalina.core.servlet.HttpServlet;
import org.apache.catalina.core.servlet.HttpServletRequest;
import org.apache.catalina.core.servlet.HttpServletResponse;
import org.apache.catalina.core.session.SessionManager;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public class Container {

    private final RequestHandlerAdaptor requestHandlerAdaptor;
    private final SessionManager sessionManager;

    public Container(final Set<HttpServlet> handlers, final SessionManager sessionManager) {
        this.requestHandlerAdaptor = new RequestHandlerAdaptor(new RequestMapping(handlers, new DefaultServlet()));
        this.sessionManager = sessionManager;
    }

    public Response handle(final Request request) {
        final var httpServletRequest = new HttpServletRequest(request, sessionManager);
        final var httpServletResponse = new HttpServletResponse();

        return requestHandlerAdaptor.service(httpServletRequest, httpServletResponse);
    }

}
