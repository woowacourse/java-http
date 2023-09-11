package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController<T> implements Controller {

    protected final T service;

    protected AbstractController(T service) {
        this.service = service;
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.isSameHttpMethod(HttpMethod.GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.isSameHttpMethod(HttpMethod.POST)) {
            doPost(httpRequest, httpResponse);
            return;
        }
        httpResponse.setHttpStatus(HttpStatus.NOT_FOUND)
                .setRedirect("/404.html");
    }

    protected abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse);

    protected abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse);

}
