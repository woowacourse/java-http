package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

public abstract class GetAndPostController implements Controller {

    @Override
    public void handle(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.getMethod().isGet()) {
            doGet(httpRequest, httpResponse);
            return;
        }
        doPost(httpRequest, httpResponse);
    }

    protected abstract void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse);

    protected abstract void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
