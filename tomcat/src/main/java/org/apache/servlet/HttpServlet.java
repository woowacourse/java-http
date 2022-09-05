package org.apache.servlet;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.StatusCode;

public abstract class HttpServlet implements Servlet {

    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.getHttpMethod() == HttpMethod.GET) {
            doGet(httpRequest, httpResponse);
            return;
        }

        if (httpRequest.getHttpMethod() == HttpMethod.POST) {
            doPost(httpRequest, httpResponse);
            return;
        }

        httpResponse.addStatusCode(StatusCode.NOT_FOUND);
        httpResponse.addView("404.html");
    }

    protected abstract void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse);

    protected abstract void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
