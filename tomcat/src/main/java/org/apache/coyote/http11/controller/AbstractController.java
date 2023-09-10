package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request, final HttpResponse response) {
        if (request.methodIsEqualTo(HttpMethod.GET)) {
            return doGet(request, response);
        }
        if (request.methodIsEqualTo(HttpMethod.POST)) {
            return doPost(request, response);
        }
        return null;
    }

    protected abstract HttpResponse doPost(final HttpRequest request, final HttpResponse response);

    protected abstract HttpResponse doGet(final HttpRequest request, final HttpResponse response);

}
