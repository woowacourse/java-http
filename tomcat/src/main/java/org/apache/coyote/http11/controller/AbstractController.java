package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.exception.NoSuchApiException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;

public abstract class AbstractController implements Controller {


    @Override
    public void service(Request request, Response response) {
        if (request.getMethod().equals(HttpMethod.POST)) {
            doPost(request, response);
            return;
        }
        doGet(request, response);
    }

    protected void doPost(Request request, Response response) {
        throw new NoSuchApiException();
    }

    protected void doGet(Request request, Response response) {
        throw new NoSuchApiException();
    }
}
