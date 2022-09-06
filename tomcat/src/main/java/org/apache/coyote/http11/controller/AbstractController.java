package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {
    @Override
    public HttpResponse service(HttpRequest httpRequest) {
        if (httpRequest.getHttpMethod() == HttpMethod.POST) {
            return doPost(httpRequest);
        }
        return doGet(httpRequest);
    }

    protected abstract HttpResponse doGet(HttpRequest request);

    protected abstract HttpResponse doPost(HttpRequest request);
}
