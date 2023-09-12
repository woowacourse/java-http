package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httprequest.RequestMethod;
import org.apache.coyote.httpresponse.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest httpRequest, HttpResponse httpResponse) {
        if (httpRequest.isSameRequestMethod(RequestMethod.GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.isSameRequestMethod(RequestMethod.POST)) {
            doPost(httpRequest, httpResponse);
            return;
        }
        new MethodNotAllowedController().service(httpRequest, httpResponse);
    }

    protected abstract void doPost(final HttpRequest httpRequest, HttpResponse httpResponse);

    protected abstract void doGet(final HttpRequest httpRequest, HttpResponse httpResponse);
}
