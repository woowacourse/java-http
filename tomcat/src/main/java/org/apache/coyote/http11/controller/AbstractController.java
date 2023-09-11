package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.request.Request;
import org.apache.coyote.http11.message.response.Response;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final Request request, final Response response) throws Exception {
        if (request.isMatchMethod(HttpMethod.GET)) {
            doGet(request, response);
        }
        if (request.isMatchMethod(HttpMethod.POST)) {
            doPost(request, response);
        }
    }

    protected abstract void doPost(Request request, Response response) throws Exception;

    protected abstract void doGet(Request request, Response response) throws Exception;
}
