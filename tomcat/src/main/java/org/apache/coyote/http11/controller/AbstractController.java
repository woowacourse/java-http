package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public final HttpResponse doService(HttpRequest request) {
        if (request.getHttpMethod() == HttpMethod.GET) {
            return doGet(request);
        }

        if (request.getHttpMethod() == HttpMethod.POST) {
            return doPost(request);
        }

        return HttpResponse.notFound().build();
    }

    protected HttpResponse doGet(final HttpRequest request) {
        return HttpResponse.notFound().build();
    }

    protected HttpResponse doPost(final HttpRequest request) {
        return HttpResponse.notFound().build();
    }
}
