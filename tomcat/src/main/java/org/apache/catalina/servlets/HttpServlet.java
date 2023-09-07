package org.apache.catalina.servlets;

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

    protected abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse);

    protected abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse);
}
