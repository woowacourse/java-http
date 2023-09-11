package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(HttpRequest request) throws Exception {
        final HttpMethod httpMethod = request.getRequestLine().getHttpMethod();
        if (httpMethod == HttpMethod.GET) {
            return doGet(request);
        }
        if (httpMethod == HttpMethod.POST) {
            return doPost(request);
        }
        throw new UnsupportedOperationException();
    }

    protected HttpResponse doGet(HttpRequest request) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected HttpResponse doPost(HttpRequest request) throws Exception {
        throw new UnsupportedOperationException();
    }
}
