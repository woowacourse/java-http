package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest req, final HttpResponse res) throws Exception {
        if (req.isSameMethod(HttpMethod.GET)) {
            doGet(req, res);
        }
        if (req.isSameMethod(HttpMethod.POST)) {
            doPost(req, res);
        }
    }

    protected void doGet(final HttpRequest req, final HttpResponse resp) throws Exception {
        throw new UnsupportedOperationException();
    }

    protected void doPost(final HttpRequest req, final HttpResponse resp) throws Exception {
        throw new UnsupportedOperationException();
    }
}
