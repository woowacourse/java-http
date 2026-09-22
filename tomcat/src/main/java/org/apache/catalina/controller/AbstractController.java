package org.apache.catalina.controller;

import org.apache.catalina.Request;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponses;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse handle(final Request request) throws Exception {
        if (request.isGet()) {
            return doGet(request);
        }

        if (request.isPost()) {
            return doPost(request);
        }

        return HttpResponses.methodNotAllowed();
    }

    protected HttpResponse doGet(final Request request) throws Exception {
        return HttpResponses.methodNotAllowed();
    }

    protected HttpResponse doPost(final Request request) throws Exception {
        return HttpResponses.methodNotAllowed();
    }
}
