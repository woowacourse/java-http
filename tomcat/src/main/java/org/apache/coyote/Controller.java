package org.apache.coyote;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import java.io.IOException;

public abstract class Controller {

    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
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

    public abstract boolean canHandle(final HttpRequest httpRequest);

    public abstract void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException;
    public abstract void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException;
}
