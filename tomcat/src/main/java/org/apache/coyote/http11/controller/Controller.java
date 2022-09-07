package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class Controller {

    public void process(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.isGet()) {
            processGet(httpRequest, httpResponse);
            return;
        }
        processPost(httpRequest, httpResponse);
    }

    public abstract void processPost(final HttpRequest httpRequest, final HttpResponse httpResponse);

    public abstract void processGet(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
