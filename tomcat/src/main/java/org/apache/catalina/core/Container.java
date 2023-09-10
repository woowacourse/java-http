package org.apache.catalina.core;

import java.util.Set;
import org.apache.catalina.core.servlet.DefaultServlet;
import org.apache.catalina.core.servlet.HttpServlet;
import org.apache.catalina.core.servlet.HttpServletResponse;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.apache.coyote.http11.session.SessionManager;

public class Container {

    private final RequestHandlerAdaptor requestHandlerAdaptor;
    private final SessionManager sessionManager;

    public Container(final Set<HttpServlet> handlers, final SessionManager sessionManager) {
        this.requestHandlerAdaptor = new RequestHandlerAdaptor(new RequestMapping(handlers, new DefaultServlet()));
        this.sessionManager = sessionManager;
    }

    public Response handle(final Request request) {
        /// TODO: 2023/09/10  request에 sessionManager 담기 
        return requestHandlerAdaptor.service(request, new HttpServletResponse());
    }

}
