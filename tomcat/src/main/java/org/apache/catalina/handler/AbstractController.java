package org.apache.catalina.handler;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) {
        if (isGetRequest(request)) {
            return doGet(request);
        }
        return doPost(request);
    }

    protected abstract HttpResponse doGet(final HttpRequest request);

    protected abstract HttpResponse doPost(final HttpRequest request);

    protected boolean isGetRequest(final HttpRequest request) {
        return request.isGetRequest();
    }
}
