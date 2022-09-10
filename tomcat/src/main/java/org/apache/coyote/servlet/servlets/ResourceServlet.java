package org.apache.coyote.servlet.servlets;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.header.Method;
import org.apache.coyote.http11.response.HttpResponse;

public class ResourceServlet extends AbstractServlet {

    public ResourceServlet(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        final Method method = httpRequest.getMethod();

        if (method.isGet()) {
            doGet(httpRequest, httpResponse);
            return;
        }
        setNotFound(httpResponse);
    }

    private void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.setStatusCode("200")
            .setBody(httpRequest.getUrl());
    }
}
