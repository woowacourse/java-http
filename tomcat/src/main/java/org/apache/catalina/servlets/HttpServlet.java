package org.apache.catalina.servlets;

import org.apache.catalina.servlets.exception.MethodNotAllowedException;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class HttpServlet implements Servlet {

    @Override
    public final void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.getHttpMethod() == HttpMethod.GET) {
            doGet(httpRequest, httpResponse);
        }
        if (httpRequest.getHttpMethod() == HttpMethod.POST) {
            doPost(httpRequest, httpResponse);
        }
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new MethodNotAllowedException();
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new MethodNotAllowedException();
    }
}
