package org.apache.coyote.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class Controller {

    public void process(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.isGet()) {
            processGet(httpRequest, httpResponse);
            return;
        }
        processPost(httpRequest, httpResponse);
    }

    protected abstract void processPost(final HttpRequest httpRequest, final HttpResponse httpResponse);

    protected abstract void processGet(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
