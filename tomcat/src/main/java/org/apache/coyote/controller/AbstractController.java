package org.apache.coyote.controller;

import org.apache.coyote.controller.exception.UnsupportedRequestMethodException;
import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httprequest.RequestMethod;
import org.apache.coyote.httpresponse.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest httpRequest, final HttpResponse httpResponse) {
        if (httpRequest.isSameRequestMethod(RequestMethod.GET)) {
            doGet(httpRequest, httpResponse);
            return;
        }
        if (httpRequest.isSameRequestMethod(RequestMethod.POST)) {
            doPost(httpRequest, httpResponse);
            return;
        }
        throw new UnsupportedRequestMethodException();
    }

    protected abstract void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse);

    protected abstract void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse);
}
