package org.apache.coyote.controller;

import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

import static org.apache.coyote.http11.request.RequestMethod.GET;
import static org.apache.coyote.http11.request.RequestMethod.POST;
import static org.apache.coyote.http11.response.StatusCode.METHOD_NOT_ALLOWED;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final Request request,
                        final Response response) {
        if (request.getRequestLine().getRequestMethod() == GET) {
            doGet(request, response);
            return;
        }
        if (request.getRequestLine().getRequestMethod() == POST) {
            doPost(request, response);
            return;
        }
        response.setStatusCode(METHOD_NOT_ALLOWED);
    }

    protected abstract void doPost(final Request request,
                                   final Response response);

    protected abstract void doGet(final Request request,
                                  final Response response);
}
