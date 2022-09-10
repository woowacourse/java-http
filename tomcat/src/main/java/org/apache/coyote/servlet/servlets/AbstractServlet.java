package org.apache.coyote.servlet.servlets;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractServlet implements Servlet {

    protected final SessionManager sessionManager;

    protected AbstractServlet(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Method method = httpRequest.getMethod();

        if (method.isGet()) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (method.isPost()) {
            doPost(httpRequest, httpResponse);
            return;
        }
        setNotFound(httpResponse);
    }

    protected abstract void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse);

    protected abstract void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse);

    protected void setUnauthorized(final HttpResponse httpResponse) {
        httpResponse.setStatusCode("401")
            .setLocation("/401.html")
            .setBody("/401.html");
    }

    protected void setNotFound(final HttpResponse httpResponse) {
        httpResponse.setStatusCode("404")
            .setLocation("/404.html")
            .setBody("/404.html");
    }
}
