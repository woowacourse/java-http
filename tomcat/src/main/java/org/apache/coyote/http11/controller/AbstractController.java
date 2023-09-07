package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.exception.HttpRequestException;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) throws Exception {
        if (request.getHttpMethod() == HttpMethod.GET) {
            return doGet(request);
        } else if (request.getHttpMethod() == HttpMethod.POST) {
            return doPost(request);
        }
        throw new HttpRequestException.MethodNotAllowed();
    }

    protected abstract HttpResponse doGet(HttpRequest request) throws Exception;

    protected abstract HttpResponse doPost(HttpRequest request) throws Exception;
}
