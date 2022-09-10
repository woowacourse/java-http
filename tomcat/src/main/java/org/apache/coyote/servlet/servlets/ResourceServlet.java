package org.apache.coyote.servlet.servlets;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.header.StatusCode;

public class ResourceServlet extends AbstractServlet {

    public ResourceServlet(final SessionManager sessionManager) {
        super(sessionManager);
    }

    @Override
    protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        httpResponse.setStatusCode(StatusCode.OK)
            .setBody(httpRequest.getUrl());
    }

    @Override
    protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        setNotFound(httpResponse);
    }
}
