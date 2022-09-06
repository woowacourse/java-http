package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.domain.HttpMethod;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        HttpMethod httpMethod = httpRequest.getHttpMethod();
        if (httpMethod.equals(HttpMethod.GET)) {
            return doGet(httpRequest);
        }
        if (httpMethod.equals(HttpMethod.POST)) {
            return doPost(httpRequest);
        }
        return HttpResponse.methodNotAllowed();
    }

    protected HttpResponse doPost(final HttpRequest httpRequest) {
        return HttpResponse.methodNotAllowed();
    }

    protected HttpResponse doGet(final HttpRequest httpRequest) {
        return HttpResponse.methodNotAllowed();
    }
}
