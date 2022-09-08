package org.apache.coyote.servlet.servlets;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractServlet implements Servlet {

    protected final SessionManager sessionManager;

    protected AbstractServlet(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public abstract HttpResponse service(final HttpRequest httpRequest);

    protected HttpResponse createNotFoundResponse(final HttpRequest httpRequest) {
        return HttpResponse.of(httpRequest, "/404.html", "404");
    }
}
