package org.apache.coyote.http11.handler;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class GetAndPostHandler implements Handler {

    @Override
    public HttpResponse handle(final HttpRequest httpRequest) {
        if (httpRequest.getMethod().isGet()) {
            return doGet(httpRequest);
        }
        return doPost(httpRequest);
    }

    protected abstract HttpResponse doGet(final HttpRequest httpRequest);

    protected abstract HttpResponse doPost(final HttpRequest httpRequest);
}
