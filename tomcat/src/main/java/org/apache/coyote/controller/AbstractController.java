package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httprequest.RequestMethod;
import org.apache.coyote.httpresponse.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        if (httpRequest.isSameRequestMethod(RequestMethod.GET)) {
            return doGet(httpRequest);
        }
        if (httpRequest.isSameRequestMethod(RequestMethod.POST)) {
            return doPost(httpRequest);
        }
        return new MethodNotAllowedController().service(httpRequest);
    }

    protected abstract HttpResponse doPost(final HttpRequest httpRequest);

    protected abstract HttpResponse doGet(final HttpRequest httpRequest);
}
