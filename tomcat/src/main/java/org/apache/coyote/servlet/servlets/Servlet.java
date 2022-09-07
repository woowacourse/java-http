package org.apache.coyote.servlet.servlets;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class Servlet {

    private static Servlet servlet;

    public abstract Servlet init();

    public abstract HttpResponse service(final HttpRequest httpRequest);

    void destroy() {
        servlet = null;
    }

    protected HttpResponse createNotFoundResponse(final HttpRequest httpRequest) {
        return HttpResponse.of(httpRequest.getHttpVersion(), "/login.html", "200");
    }
}
