package org.apache.coyote.handler;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;

public abstract class AbstractRequestHandler implements RequestHandler {

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.isGet()) {
            get(httpRequest, httpResponse);
        }
        if (httpRequest.isPost()) {
            post(httpRequest, httpResponse);
        }
    }

    protected abstract void get(HttpRequest httpRequest, HttpResponse httpResponse);

    protected abstract void post(HttpRequest httpRequest, HttpResponse httpResponse);
}
