package org.apache.coyote.handler;

import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.HttpMethod;

public abstract class AbstractRequestHandler implements RequestHandler {

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (HttpMethod.isGet(httpRequest.getMethod())) {
            get(httpRequest, httpResponse);
        }
        if (HttpMethod.isPost(httpRequest.getMethod())) {
            post(httpRequest, httpResponse);
        }
    }

    protected abstract void get(HttpRequest httpRequest, HttpResponse httpResponse);

    protected abstract void post(HttpRequest httpRequest, HttpResponse httpResponse);
}
