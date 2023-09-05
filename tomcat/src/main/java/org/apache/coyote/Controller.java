package org.apache.coyote;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class Controller {

    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (!canHandle(httpRequest)) {
            // TODO: exception - there is any controller to handle
        }
        final String method = httpRequest.getMethod();
        if ("GET".equals(method)) {
            doGet(httpRequest, httpResponse);
        }
        if ("POST".equals(method)) {
            doPost(httpRequest, httpResponse);
        }
        // TODO: exception - invalid http method
    }

    public abstract boolean canHandle(final HttpRequest target);

    public abstract HttpResponse doGet(final HttpRequest httpRequest, final HttpResponse httpResponse);
    public abstract HttpResponse doPost(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
